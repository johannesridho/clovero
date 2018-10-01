package clovero.dashboard.quizsolver;

import clovero.datatables.DatatablesColumn;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesSpecification;
import clovero.quizsolver.QuizSolver;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import static clovero.datatables.DatatablesColumn.toCamelCase;

public class QuizSolverDatatablesSpecification extends DatatablesSpecification<QuizSolver> {

    public QuizSolverDatatablesSpecification(DatatablesCriteria criteria) {
        super(criteria);
    }

    @Override
    protected <Y> Path<Y> getColumnName(Root<QuizSolver> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn) {
        final String column = toCamelCase(dtColumn.getName());

        if (column.contains("user.")) {
            return root.join("user").get(column.replace("user.", ""));
        }

        if (column.contains("quiz.")) {
            return root.join("quiz").get(column.replace("quiz.", ""));
        }

        return root.get(column);
    }

    @Override
    protected boolean isDateColumn(String name) {
        return name.equals("created_at");
    }
}
