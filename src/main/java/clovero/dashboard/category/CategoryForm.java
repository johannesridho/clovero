package clovero.dashboard.category;

import clovero.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Optional;

@Data
@NoArgsConstructor
public class CategoryForm {

    private Optional<Integer> id = Optional.empty();

    @NotEmpty
    private String name;
    private String description;
    private String imageUrl;

    public CategoryForm(Category category) {
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
    }
}
