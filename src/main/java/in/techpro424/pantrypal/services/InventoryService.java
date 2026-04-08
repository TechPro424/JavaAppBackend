package in.techpro424.pantrypal.services;

import in.techpro424.pantrypal.models.Ingredient;
import in.techpro424.pantrypal.repositories.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {
    private final IngredientRepository ingredientRepository;
    public InventoryService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        if (ingredient.getExpirationDate() == null) ingredient.setExpirationDate(LocalDate.now().plusDays(7));
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> findAllItemsExpiringSoon(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return ingredientRepository.findIngredientsByExpirationDateBefore(cutoffDate);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkForExpiringItems() {
        List<Ingredient> expiringSoon = findAllItemsExpiringSoon(2);
        if (!expiringSoon.isEmpty()) {
            System.out.println("ALERT: The following items are expiring soon!");
            expiringSoon.forEach(item -> System.out.println("- " + item.getName() + " (expires on " + item.getExpirationDate() + ")"));
        }
    }

    public void deleteIngredientById(String id) {
        Ingredient ingredient = ingredientRepository.findIngredientById(id);
        if (ingredient == null) throw new EntityNotFoundException("Ingredient not found");
        else ingredientRepository.deleteById(id);
    }

}
