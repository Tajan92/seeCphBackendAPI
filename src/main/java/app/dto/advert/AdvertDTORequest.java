package app.dto.advert;

import app.enums.AddPlacement;

import java.time.LocalDate;

public record AdvertDTORequest(
        int eventId,
        int userId,
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {
}
