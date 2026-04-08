package in.techpro424.pantrypal.controllers;

import in.techpro424.pantrypal.models.Ingredient;
import in.techpro424.pantrypal.services.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RestTemplate restTemplate;

    @Value("${spoonacular.key}")
    String spoonApiKey;

    public RecipeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/suggested")
    public ResponseEntity<?> getSuggestedRecipes(@RequestBody List<String> expiringIngredientNames) {
        
        if (expiringIngredientNames.isEmpty()) {
            return ResponseEntity.ok(List.of("No items expiring soon. You're good!"));
        }

        String ingredientsStr = String.join(",", expiringIngredientNames);

        String spoonacularUrl = "https://api.spoonacular.com/recipes/findByIngredients" +
                                "?ingredients=" + ingredientsStr +
                                "&number=3" +
                                "&apiKey=" + spoonApiKey;

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(spoonacularUrl, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of(
                "Fallback Recipe 1: Salad with " + ingredientsStr,
                "Fallback Recipe 2: Stir Fry with " + ingredientsStr
            ));
        }
    }
}
