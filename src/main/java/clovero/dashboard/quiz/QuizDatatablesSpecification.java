package clovero.dashboard.quiz;

import clovero.datatables.DatatablesColumn;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesSpecification;
import clovero.quiz.Quiz;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import static clovero.datatables.DatatablesColumn.toCamelCase;

public class QuizDatatablesSpecification extends DatatablesSpecification<Quiz> {

    public QuizDatatablesSpecification(DatatablesCriteria criteria) {
        super(criteria);
    }

    @Override
    protected <Y> Path<Y> getColumnName(Root<Quiz> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn) {
        final String column = toCamelCase(dtColumn.getName());

        if (column.contains("category.")) {
            return root.join("category").get(column.replace("category.", ""));
        }

        return root.get(column);
    }

    @Override
    protected boolean isDateColumn(String name) {
        return name.equals("created_at") || name.equals("updated_at");
    }
}
