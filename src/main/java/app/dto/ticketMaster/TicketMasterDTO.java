package app.dto.ticketMaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketMasterDTO(
        @JsonProperty("_embedded") Embedded embedded,
        Page page
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Embedded(List<TmEvent> events) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Page(int size, int totalElements, int totalPages, int number) {}
}
