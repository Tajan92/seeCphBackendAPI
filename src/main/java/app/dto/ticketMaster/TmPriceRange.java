package app.dto.ticketMaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmPriceRange(Double min, Double max, String currency) {}
