package app.dto.ticketMaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmDates(TmStart start) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmStart(
            String localDate,   // parse to LocalDate yourself
            String localTime,   // parse to LocalTime yourself
            String dateTime
    ) {}
}
