package app.mapper;

import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;
import app.entities.users.User;

import java.util.List;

public class AdvertConverter {
    private final EventConverter eventConverter = new EventConverter();
    private final UserConverter userConverter = new UserConverter();

    public AdvertDTOResponse convertEntityToDTO(Advert advert) {
        User user = advert.getOrganizer() != null ? advert.getOrganizer() : advert.getAdmin();
        return new AdvertDTOResponse(
                advert.getEvent() != null ? eventConverter.convertEntityToDTO(advert.getEvent()) : null,
                user != null ? userConverter.convertEntityToDTO(user) : null,
                advert.getAdvertId(),
                advert.getAddPlacement(),
                advert.getStartDate(),
                advert.getEndDate());
    }

    public List<AdvertDTOResponse> convertEntityListToDTOList(List<Advert> adverts) {
        return adverts.stream().map(this::convertEntityToDTO).toList();
    }
}