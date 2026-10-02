package app.service;

import app.dao.AdvertDAO;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.dto.event.EventDTOResponse;
import app.dto.user.UserDTOResponse;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.mapper.UserConverter;

import java.util.ArrayList;
import java.util.List;

public class AdvertService implements IService<AdvertDTORequest, AdvertDTOResponse> {
    private final AdvertDAO advertDAO;
    private final UserDAO userDAO;
    private final EventDAO eventDAO;
    private final EventService eventService;
    private final UserService userService;
    private final UserConverter userConverter = new UserConverter();

    public AdvertService(AdvertDAO advertDAO, UserDAO userDAO, EventDAO eventDAO, EventService eventService, UserService userService) {
        this.advertDAO = advertDAO;
        this.userDAO = userDAO;
        this.eventDAO = eventDAO;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public AdvertDTOResponse create(AdvertDTORequest input) {
        Event event = eventDAO.readById(input.eventId());
        User user = userDAO.readById(input.userId());

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
        return new AdvertDTOResponse(createdAdvert, userService.getById(input.userId()), eventService.getById(input.eventId()));
    }

    @Override
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

        EventDTOResponse eventDTOResponse = new EventDTOResponse(updatedAdvert.getEvent());
        UserDTOResponse userDTOResponse = userConverter.convertEntityToDTO(user);

        return new AdvertDTOResponse(updatedAdvert, userDTOResponse, eventDTOResponse);
    }

    @Override
    public AdvertDTOResponse getById(int id) {
        Advert advert = advertDAO.readById(id);
        EventDTOResponse eventDTOResponse = new EventDTOResponse(advert.getEvent());
        UserDTOResponse userDTOResponse =  getUserDTOResponse(advert);

        return new AdvertDTOResponse(advert, userDTOResponse, eventDTOResponse);
    }

    @Override
    public List<AdvertDTOResponse> getAll() {
        List<AdvertDTOResponse> advertDTOResponses = new ArrayList<>();
        List<Advert> adverts = advertDAO.readAll();
        if (adverts != null && !adverts.isEmpty()) {
            for (Advert advert : adverts) {
                EventDTOResponse eventDTOResponse = new EventDTOResponse(advert.getEvent());
                UserDTOResponse userDTOResponse =  getUserDTOResponse(advert);

                advertDTOResponses.add(new AdvertDTOResponse(advert, userDTOResponse, eventDTOResponse));
            }
        }
        return advertDTOResponses;
    }

    @Override
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
