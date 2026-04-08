package in.techpro424.pantrypal.controllers;

import in.techpro424.pantrypal.models.Ingredient;
import in.techpro424.pantrypal.services.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final RestTemplate restTemplate;

    @Value("${ocr.key}")
    String OcrApiKey;

    public ReceiptController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<List<Ingredient>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String ocrUrl = "https://api.ocr.space/parse/image";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("apikey", OcrApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename(){
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt.jpg";
                }
            };
            body.add("file", fileAsResource);
            body.add("language", "eng");
            body.add("isOverlayRequired", "false");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(ocrUrl, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            String parsedText = "";
            if (responseBody != null && responseBody.containsKey("ParsedResults")) {
                List<Map<String, Object>> parsedResults = (List<Map<String, Object>>) responseBody.get("ParsedResults");
                if (!parsedResults.isEmpty()) {
                    parsedText = (String) parsedResults.getFirst().get("ParsedText");
                }
            }

            List<Ingredient> addedIngredients = parseTextToIngredients(parsedText);

            return ResponseEntity.ok(addedIngredients);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private List<Ingredient> parseTextToIngredients(String text) {
        Map<String, Integer> shelfLifeMap = Map.of(
            "milk", 7,
            "eggs", 21,
            "bread", 5,
            "chicken", 3,
            "beef", 4,
            "cheese", 14,
            "apple", 10,
            "banana", 5,
            "spinach", 4
        );

        List<Ingredient> foundItems = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (Map.Entry<String, Integer> entry : shelfLifeMap.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                Ingredient item = new Ingredient();
                item.setId(UUID.randomUUID().toString());
                item.setName(capitalize(entry.getKey()));
                item.setDateAdded(LocalDate.now());
                item.setExpirationDate(LocalDate.now().plusDays(entry.getValue()));
                foundItems.add(item);
            }
        }
        
        if (foundItems.isEmpty()) {
            Ingredient dummy = new Ingredient();
            dummy.setId(UUID.randomUUID().toString());
            dummy.setName("Milk (Fallback)");
            dummy.setDateAdded(LocalDate.now());
            dummy.setExpirationDate(LocalDate.now().plusDays(7));
            foundItems.add(dummy);
        }

        return foundItems;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
