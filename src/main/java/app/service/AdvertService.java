package app.service;

import app.dao.AdvertDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;

import java.util.ArrayList;
import java.util.List;

public class AdvertService implements IService<AdvertDTORequest, AdvertDTOResponse> {
    private final AdvertDAO advertDAO;

    public AdvertService(AdvertDAO advertDAO) {
        this.advertDAO = advertDAO;
    }

    @Override
    public AdvertDTOResponse create(AdvertDTORequest input) {
        Advert advert = Advert.builder()
                .addPlacement(input.addPlacement())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .build();
        Advert createdAdvert = advertDAO.create(advert);
        return new AdvertDTOResponse(createdAdvert);
    }

    @Override
    public AdvertDTOResponse updateById(int id, AdvertDTORequest input) {
        Advert configuredAdvert = Advert.builder()
                .advertId(id)
                .addPlacement(input.addPlacement())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .build();
        Advert updatedAdvert = advertDAO.update(configuredAdvert);
        return new AdvertDTOResponse(updatedAdvert);
    }

    @Override
    public AdvertDTOResponse getById(int id) {
        Advert advert = advertDAO.readById(id);
        return new AdvertDTOResponse(advert);
    }

    @Override
    public List<AdvertDTOResponse> getAll() {
        List<AdvertDTOResponse> advertDTOResponses = new ArrayList<>();
        List<Advert> adverts = advertDAO.readAll();
        if (adverts != null && !adverts.isEmpty()) {
            for (Advert advert : adverts) {
                advertDTOResponses.add(new AdvertDTOResponse(advert));
            }
        }
        return advertDTOResponses;
    }

    @Override
    public boolean deleteById(int id) {
        Advert advert = advertDAO.readById(id);
        return advertDAO.delete(advert);
    }
}
