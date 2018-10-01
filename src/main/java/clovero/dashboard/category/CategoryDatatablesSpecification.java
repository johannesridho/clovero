package clovero.dashboard.category;

import clovero.category.Category;
import clovero.datatables.DatatablesColumn;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import static clovero.datatables.DatatablesColumn.toCamelCase;

public class CategoryDatatablesSpecification extends DatatablesSpecification<Category> {

    public CategoryDatatablesSpecification(DatatablesCriteria criteria) {
        super(criteria);
    }

    @Override
    protected <Y> Path<Y> getColumnName(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn) {
        final String column = toCamelCase(dtColumn.getName());

        return root.get(column);
    }

    @Override
    protected boolean isDateColumn(String name) {
        return name.equals("created_at") || name.equals("updated_at");
    }
}
