package clovero.dashboard.category;

import clovero.category.Category;
import clovero.category.CategoryRepository;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CategoryDashboardService {

    CategoryRepository categoryRepository;

    public DatatablesResponse findWithDatatablesCriteria(DatatablesCriteria criteria) {
        final Page<Category> categories = categoryRepository.findAll(
                new CategoryDatatablesSpecification(criteria),
                criteria.getPageRequest());

        return new DatatablesResponse(
                categories.getContent(),
                categories.getTotalElements(),
                categoryRepository.count(),
                criteria.getDraw());
    }

    public Optional<Category> find(Integer id) {
        return categoryRepository.findOne(id);
    }

    public Category save(CategoryForm form) {
        final Category category;
        if (form.getId().isPresent()) {
            category = categoryRepository.findOne(form.getId().get()).orElse(new Category());
        } else {
            category = new Category();
        }

        category.setName(form.getName());
        category.setDescription(form.getDescription());
        category.setImageUrl(form.getImageUrl());

        return categoryRepository.save(category);
    }

    public void delete(Integer id) {
        categoryRepository.delete(id);
    }
}
