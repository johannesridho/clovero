package clovero.datatables;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatatablesCriteria implements Serializable {

    private static final long serialVersionUID = 8661357461501153387L;

    private static final Pattern pattern = Pattern.compile("columns\\[([0-9]*)?\\]");

    private static final String rangeSearchFilterDelimiter = "-yadcf_delim-";

    private final String search;
    private final Integer start;
    private final Integer length;
    private final List<DatatablesColumn> datatablesColumns;
    private final List<DatatablesColumn> sortedDatatablesColumns;
    private final Integer draw;

    public DatatablesCriteria(String search, Integer displayStart, Integer displaySize, List<DatatablesColumn> datatablesColumns,
                              List<DatatablesColumn> sortedDatatablesColumns, Integer draw) {
        this.search = search;
        this.start = displayStart;
        this.length = displaySize;
        this.datatablesColumns = datatablesColumns;
        this.sortedDatatablesColumns = sortedDatatablesColumns;
        this.draw = draw;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getLength() {
        return length;
    }

    public String getSearch() {
        return search;
    }

    public Integer getDraw() {
        return draw;
    }

    public List<DatatablesColumn> getDatatablesColumns() {
        return datatablesColumns;
    }

    /**
     * @return all sorted columns.
     */
    public List<DatatablesColumn> getSortedDatatablesColumns() {
        return sortedDatatablesColumns;
    }

    public Pageable getPageRequest() {
        final int pageNumber = (int) Math.ceil((double) start / length);
        return new PageRequest(pageNumber, length);
    }

    public boolean hasColumnSearch() {
        return this.hasOneSearchableColumn() && this.hasOneFilteredColumn();
    }

    /**
     * @return {@code true} if one the columns is searchable, {@code false}
     * otherwise.
     */
    public Boolean hasOneSearchableColumn() {
        for (DatatablesColumn datatablesColumn : this.datatablesColumns) {
            if (datatablesColumn.isSearchable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if a column is being filtered, false otherwise.
     */
    public Boolean hasOneFilteredColumn() {
        for (DatatablesColumn datatablesColumn : this.datatablesColumns) {
            if (isNotBlank(datatablesColumn.getSearch()) || isNotBlank(datatablesColumn.getSearchFrom())
                    || isNotBlank(datatablesColumn.getSearchTo())) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Map all request parameters into a wrapper POJO that eases SQL querying.
     * </p>
     *
     * @param request The request sent by Datatables containing all parameters.
     * @return a wrapper POJO.
     */
    public static DatatablesCriteria getFromRequest(HttpServletRequest request) {

        int columnNumber = getColumnNumber(request);

        String paramSearch = request.getParameter("search[value]");
        String paramDraw = request.getParameter("draw");
        String paramStart = request.getParameter("start");
        String paramLength = request.getParameter("length");

        Integer draw = isNotBlank(paramDraw) ? Integer.parseInt(paramDraw) : -1;
        Integer start = isNotBlank(paramStart) ? Integer.parseInt(paramStart) : -1;
        Integer length = isNotBlank(paramLength) ? Integer.parseInt(paramLength) : -1;

        // Column definitions
        List<DatatablesColumn> datatablesColumns = new ArrayList<DatatablesColumn>();

        for (int i = 0; i < columnNumber; i++) {

            DatatablesColumn datatablesColumn = new DatatablesColumn();

            datatablesColumn.setName(request.getParameter("columns[" + i + "][data]"));
            datatablesColumn.setSearchable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][searchable]")));
            datatablesColumn.setSortable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][orderable]")));
            datatablesColumn.setRegex(request.getParameter("columns[" + i + "][search][regex]"));

            String searchTerm = request.getParameter("columns[" + i + "][search][value]");

            if (isNotBlank(searchTerm)) {
                datatablesColumn.setFiltered(true);
                String[] splittedSearch = searchTerm.split(rangeSearchFilterDelimiter);
                if (searchTerm.equals(rangeSearchFilterDelimiter)) {
                    datatablesColumn.setSearch("");
                } else if (searchTerm.startsWith(rangeSearchFilterDelimiter)) {
                    datatablesColumn.setSearchTo(splittedSearch[1]);
                } else if (searchTerm.endsWith(rangeSearchFilterDelimiter)) {
                    datatablesColumn.setSearchFrom(splittedSearch[0]);
                } else if (searchTerm.contains(rangeSearchFilterDelimiter)) {
                    datatablesColumn.setSearchFrom(splittedSearch[0]);
                    datatablesColumn.setSearchTo(splittedSearch[1]);
                } else {
                    datatablesColumn.setSearch(searchTerm);
                }
            }

            for (int j = 0; j < columnNumber; j++) {
                String ordered = request.getParameter("order[" + j + "][column]");
                if (ordered != null && ordered.equals(String.valueOf(i))) {
                    datatablesColumn.setSorted(true);
                    break;
                }
            }

            datatablesColumns.add(datatablesColumn);
        }

        // Sorted column definitions
        List<DatatablesColumn> sortedDatatablesColumns = new LinkedList<DatatablesColumn>();

        for (int i = 0; i < columnNumber; i++) {
            String paramSortedCol = request.getParameter("order[" + i + "][column]");

            // The column is being sorted
            if (isNotBlank(paramSortedCol)) {
                Integer sortedCol = Integer.parseInt(paramSortedCol);
                DatatablesColumn sortedDatatablesColumn = datatablesColumns.get(sortedCol);
                String sortedColDirection = request.getParameter("order[" + i + "][dir]");
                if (isNotBlank(sortedColDirection)) {
                    sortedDatatablesColumn.setSortDirection(DatatablesColumn.SortDirection.valueOf(sortedColDirection.toUpperCase(Locale.ENGLISH)));
                }

                sortedDatatablesColumns.add(sortedDatatablesColumn);
            }
        }

        return new DatatablesCriteria(paramSearch, start, length, datatablesColumns, sortedDatatablesColumns, draw);
    }

    public static DatatablesCriteria getFromRequestIgnorePagination(HttpServletRequest request, int maxRow) {
        final DatatablesCriteria datatablesCriteria = getFromRequest(request);
        return new DatatablesCriteria(datatablesCriteria.search, 0, maxRow, datatablesCriteria.getDatatablesColumns(), datatablesCriteria.getSortedDatatablesColumns(), datatablesCriteria.getDraw());
    }

    private static int getColumnNumber(HttpServletRequest request) {

        int columnNumber = 0;
        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements(); ) {
            String param = e.nextElement();
            Matcher matcher = pattern.matcher(param);
            while (matcher.find()) {
                Integer col = Integer.parseInt(matcher.group(1));
                if (col > columnNumber) {
                    columnNumber = col;
                }
            }
        }

        if (columnNumber != 0) {
            columnNumber++;
        }
        return columnNumber;
    }

    private static boolean isNotBlank(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return !str.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "DatatablesCriteria [search=" + search + ", start=" + start + ", length=" + length + ", datatablesColumns="
                + datatablesColumns + ", sortingColumnDefs=" + sortedDatatablesColumns + ", draw=" + draw + "]";
    }
}
