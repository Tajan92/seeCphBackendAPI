package app.dto.advert;

import app.entities.Advert;
import app.enums.AddPlacement;

import java.time.LocalDate;

public record AdvertDTORequest(
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {
}
