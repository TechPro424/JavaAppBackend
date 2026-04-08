package in.techpro424.pantrypal.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Ingredient {

    @Getter @Setter @Id String id;
    @Getter @Setter String name;
    @Getter @Setter LocalDate dateAdded;
    @Getter @Setter LocalDate expirationDate;
}
