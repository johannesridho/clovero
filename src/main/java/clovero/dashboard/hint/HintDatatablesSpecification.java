package clovero.dashboard.hint;

import clovero.datatables.DatatablesColumn;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesSpecification;
import clovero.hint.Hint;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import static clovero.datatables.DatatablesColumn.toCamelCase;

public class HintDatatablesSpecification extends DatatablesSpecification<Hint> {

    public HintDatatablesSpecification(DatatablesCriteria criteria) {
        super(criteria);
    }

    @Override
    protected <Y> Path<Y> getColumnName(Root<Hint> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn) {
        final String column = toCamelCase(dtColumn.getName());

        if (column.contains("quiz.")) {
            return root.join("quiz").get(column.replace("quiz.", ""));
        }

        return root.get(column);
    }

    @Override
    protected boolean isDateColumn(String name) {
        return name.equals("created_at") || name.equals("updated_at");
    }
}
