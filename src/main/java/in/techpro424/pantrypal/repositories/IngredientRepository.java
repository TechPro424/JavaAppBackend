package in.techpro424.pantrypal.repositories;

import in.techpro424.pantrypal.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, String> {
    List<Ingredient> findIngredientsByExpirationDateBefore(LocalDate expirationDateBefore);

    Ingredient findIngredientById(String id);
}
