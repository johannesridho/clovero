package clovero.datatables;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DatatablesResponse {

    @JsonProperty("data")
    private final List data;

    @JsonProperty("recordsFiltered")
    private final long recordsFiltered;

    @JsonProperty("recordsTotal")
    private final long recordsTotal;

    @JsonProperty("draw")
    private final int draw;

    public DatatablesResponse(List data, long recordsFiltered, long recordsTotal, int draw) {
        this.data = data;
        this.recordsFiltered = recordsFiltered;
        this.recordsTotal = recordsTotal;
        this.draw = draw;
    }

    public List getData() {
        return data;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public int getDraw() {
        return draw;
    }
}
