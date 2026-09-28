package app.dto.advert;

import app.entities.Advert;
import app.enums.AddPlacement;
import java.time.LocalDate;

public record AdvertDTOResponse(
        Integer id,
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {
    public AdvertDTOResponse(Advert advert) {
        this(
                advert.getId(),
                advert.getAddPlacement(),
                advert.getStartDate(),
                advert.getEndDate());
    }
}
