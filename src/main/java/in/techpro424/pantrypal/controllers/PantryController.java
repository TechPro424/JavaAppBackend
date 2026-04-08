package in.techpro424.pantrypal.controllers;

import in.techpro424.pantrypal.models.Ingredient;
import in.techpro424.pantrypal.services.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    private final InventoryService inventoryService;

    public PantryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return inventoryService.getAllIngredients();
    }

    @PostMapping
    public Ingredient addIngredient(@RequestBody Ingredient ingredient) {
        return inventoryService.saveIngredient(ingredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable String id) {
        try {
            inventoryService.deleteIngredientById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
