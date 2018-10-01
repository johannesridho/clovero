package clovero.datatables;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class DatatablesSpecification<T> implements Specification<T> {
    protected final DatatablesCriteria criteria;

    public DatatablesSpecification(DatatablesCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.orderBy(getOrders(root, query, cb));

        final List<Predicate> predicates = new ArrayList<>();

        if (criteria.hasColumnSearch()) {
            for (DatatablesColumn column : criteria.getDatatablesColumns()) {
                if (column.isSearchable()) {
                    final Path dbColumnName = getColumnName(root, query, cb, column);

                    if (!isEmpty(column.getSearchFrom())) {
                        predicates.add(generateSearchFromPredicate(cb, dbColumnName, column));
                    }

                    if (!isEmpty(column.getSearchTo())) {
                        predicates.add(generateSearchToPredicate(cb, dbColumnName, column));
                    }

                    if (!isEmpty(column.getSearch())) {
                        predicates.add(generateLikePredicate(cb, dbColumnName, column));
                    }
                }
            }
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    protected Predicate generateSearchFromPredicate(CriteriaBuilder cb, Path dbColumnName, DatatablesColumn datatablesColumn) {
        final String searchFrom = datatablesColumn.getSearchFrom();

        if (isDateColumn(datatablesColumn.getName())) {
            return cb.greaterThanOrEqualTo(dbColumnName, ZonedDateTime.parse(searchFrom));
        } else if (isValidNumber(searchFrom)) {
            return cb.greaterThanOrEqualTo(dbColumnName, searchFrom);
        }

        return cb.conjunction();
    }

    protected Predicate generateSearchToPredicate(CriteriaBuilder cb, Path dbColumnName, DatatablesColumn datatablesColumn) {
        final String searchTo = datatablesColumn.getSearchTo();

        if (isDateColumn(datatablesColumn.getName())) {
            return cb.lessThanOrEqualTo(dbColumnName, ZonedDateTime.parse(searchTo));
        } else if (isValidNumber(searchTo)) {
            return cb.lessThanOrEqualTo(dbColumnName, searchTo);
        }

        return cb.conjunction();
    }

    protected Predicate generateLikePredicate(CriteriaBuilder cb, Path dbColumnName, DatatablesColumn column) {
        final Expression<String> dbColumnNameAsString = dbColumnName.as(String.class);

        return cb.like(dbColumnNameAsString, getSearchString(column));
    }

    protected String getLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase(Locale.ENGLISH) + "%";
        }
    }

    protected abstract <Y> Path<Y> getColumnName(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn);

    protected abstract boolean isDateColumn(String name);

    protected boolean isExactEqualColumn(String name) {
        return false;
    }

    protected List<Order> getOrders(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        final List<Order> orders = new ArrayList<>();

        for (DatatablesColumn column : criteria.getSortedDatatablesColumns()) {
            final Order order = column.getSortDirection().equals(DatatablesColumn.SortDirection.ASC) ?
                    cb.asc(getColumnName(root, query, cb, column)) :
                    cb.desc(getColumnName(root, query, cb, column));

            orders.add(order);
        }

        return orders;
    }

    private String getSearchString(DatatablesColumn column) {
        return isExactEqualColumn(column.getName()) ?
                column.getSearch() : getLikePattern(column.getSearch());
    }

    private boolean isEmpty(String string) {
        return string == null || string.trim().equals("");
    }

    private boolean isValidNumber(String str) {
        return str.matches("^-?\\d+$");
    }
}
