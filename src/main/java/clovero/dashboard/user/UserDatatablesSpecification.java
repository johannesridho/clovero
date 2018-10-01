package clovero.dashboard.user;

import clovero.datatables.DatatablesColumn;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesSpecification;
import clovero.user.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import static clovero.datatables.DatatablesColumn.toCamelCase;

public class UserDatatablesSpecification extends DatatablesSpecification<User> {

    public UserDatatablesSpecification(DatatablesCriteria criteria) {
        super(criteria);
    }

    @Override
    protected <Y> Path<Y> getColumnName(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb, DatatablesColumn dtColumn) {
        final String column = toCamelCase(dtColumn.getName());

        return root.get(column);
    }

    @Override
    protected boolean isDateColumn(String name) {
        return name.equals("created_at") || name.equals("updated_at");
    }
}
