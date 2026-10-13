package app.dto.advert;

import app.enums.AddPlacement;

import java.time.LocalDate;

public record AdvertDTORequest(
        int eventId,
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {
}
