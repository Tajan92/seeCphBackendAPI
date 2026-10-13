package app.service;

import app.dao.AdvertDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.dto.user.UserDTOResponse;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.mapper.AdvertConverter;
import app.mapper.UserConverter;
import java.util.List;

public class AdvertService {
    private final AdvertDAO advertDAO;
    private final UserDAO userDAO;
    private final EventDAO eventDAO;
    private final UserConverter userConverter = new UserConverter();
    private final AdvertConverter advertConverter = new AdvertConverter();

    public AdvertService(AdvertDAO advertDAO, UserDAO userDAO, EventDAO eventDAO) {
        this.advertDAO = advertDAO;
        this.userDAO = userDAO;
        this.eventDAO = eventDAO;
    }

    public AdvertDTOResponse create(AdvertDTORequest input, int userID) {
        Event event = eventDAO.readById(input.eventId());
        User user = userDAO.readById(userID);

        Advert advert = Advert.builder()
                .event(event)
                .addPlacement(input.addPlacement())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .build();

        if (user instanceof Organizer){
            advert.setOrganizer((Organizer) user);
        }
        if (user instanceof Admin){
            advert.setAdmin((Admin) user);
        }
        Advert createdAdvert = advertDAO.create(advert);
        return advertConverter.convertEntityToDTO(createdAdvert);
    }

    public AdvertDTOResponse updateById(int id, AdvertDTORequest input) {
        Event event = eventDAO.readById(input.eventId());
        User user = userDAO.readById(input.userId());

        Advert configuredAdvert = Advert.builder()
                .advertId(id)
                .event(event)
                .addPlacement(input.addPlacement())
                .startDate(input.startDate())
                .endDate(input.endDate())
                .build();

        if (user instanceof Organizer) {
            configuredAdvert.setOrganizer((Organizer) user);
        } else if (user instanceof Admin) {
            configuredAdvert.setAdmin((Admin) user);
        }
        Advert updatedAdvert = advertDAO.update(configuredAdvert);
        return advertConverter.convertEntityToDTO(updatedAdvert);
    }

    public AdvertDTOResponse getById(int id) {
        return advertConverter.convertEntityToDTO(advertDAO.readById(id));
    }

    public List<AdvertDTOResponse> getAll() {
        return advertConverter.convertEntityListToDTOList(advertDAO.readAll());
    }

    public boolean deleteById(int id) {
        Advert advert = advertDAO.readById(id);
        return advertDAO.delete(advert);
    }

   private UserDTOResponse getUserDTOResponse(Advert advert) {
       int userId = advert.getAdmin() != null ? advert.getAdmin().getUserId() : advert.getOrganizer().getUserId();
       User user = userDAO.readById(userId);
       return userConverter.convertEntityToDTO(user);
   }
}
