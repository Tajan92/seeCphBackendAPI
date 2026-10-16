package app.service;

import app.dao.AdvertDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.exceptions.SecurityValidationException;
import app.mapper.AdvertConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class AdvertService {
    private final AdvertDAO advertDAO;
    private final UserDAO userDAO;
    private final EventDAO eventDAO;
    private final AdvertConverter advertConverter = new AdvertConverter();

    public AdvertService(AdvertDAO advertDAO, UserDAO userDAO, EventDAO eventDAO) {
        this.advertDAO = advertDAO;
        this.userDAO = userDAO;
        this.eventDAO = eventDAO;
    }

    public AdvertDTOResponse create(AdvertDTORequest input, int userID) {
        Event event = eventDAO.readById(input.eventId());
        User user = userDAO.readById(userID);

        if (user instanceof Organizer organizer) {
            boolean ownsEvent = event.getOrganizer() != null && Objects.equals(event.getOrganizer().getUserId(), organizer.getUserId());
            if (!ownsEvent) {
                throw new SecurityValidationException(403, "You can only advertise your own events");
            }
        }
        Advert advert = Advert.builder()
                .event(event)
                .addPlacement(input.addPlacement())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .build();

        if (user instanceof Organizer organizer) {
            advert.setOrganizer(organizer);
        } else if (user instanceof Admin admin) {
            advert.setAdmin(admin);
        }

        Advert createdAdvert = advertDAO.create(advert);
        return advertConverter.convertEntityToDTO(createdAdvert);
    }

    public AdvertDTOResponse updateById(int id, AdvertDTORequest input) {
        Advert advert = advertDAO.readById(id);
        Event event = eventDAO.readById(input.eventId());

        advert.setEvent(event);
        advert.setAddPlacement(input.addPlacement());
        advert.setStartDate(input.startDate());
        advert.setEndDate(input.endDate());

        return advertConverter.convertEntityToDTO(advertDAO.update(advert));
    }

    public AdvertDTOResponse getById(int id) {
        return advertConverter.convertEntityToDTO(advertDAO.readById(id));
    }

    public List<AdvertDTOResponse> getAll() {
        return advertConverter.convertEntityListToDTOList(advertDAO.readAll());
    }

    public List<AdvertDTOResponse> getByUserId(int userId) {
        return advertConverter.convertEntityListToDTOList(advertDAO.readAdvertsByUserId(userId));
    }

    public List<AdvertDTOResponse> getActive(AddPlacement placement) {
        return advertConverter.convertEntityListToDTOList(advertDAO.readActiveAdverts(placement, LocalDate.now()));
    }

    public void deleteById(int id) {
        Advert advert = advertDAO.readById(id);
        advertDAO.delete(advert);
    }
}
