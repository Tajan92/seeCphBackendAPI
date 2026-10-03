package app.service;

import app.dao.EventDAO;
import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.user.UserDTOResponse;
import app.entities.Address;
import app.entities.Event;
import app.enums.SourceProvider;
import app.enums.UserRole;
import app.mapper.TicketMasterConverter;
import app.utils.APIReader;
import app.utils.DefaultDescription;
import app.utils.GeoUtil;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
public class EventService implements IService<EventDTORequest, EventDTOResponse> {
    private final EventDAO eventDAO;
    private final AddressService addressService;
    private final UserService userService;
    private final APIReader apiReader;
    private final TicketMasterConverter converter;
    private int counter;

    public EventService(EventDAO eventDAO, AddressService addressService, UserService userService) {
        this.eventDAO = eventDAO;
        this.addressService = addressService;
        this.userService = userService;
        this.apiReader = new APIReader();
        this.converter = new TicketMasterConverter(addressService);
    }

//apiReader.getApiAsTmDTO("https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&page=$&apikey="+System.getenv("API_KEY"));

    @Override
    public EventDTOResponse create(EventDTORequest input) {
        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());

        String primaryImageUrl = input.primaryImageUrl();
        if (input.useDefaultImage()) {
            primaryImageUrl = "resources/images/default.jpg";
        }
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        Event event = Event.builder()
                .title(input.title())
                .description(input.description())
                .price(input.price())
                .free(input.free())
                .location(address)
                .longitude(coordinates.longitude())
                .latitude(coordinates.latitude())
                .startTime(input.startTime())
                .startDate(input.startDate())
                .url(input.url())
                .imageUrl(primaryImageUrl)
                .category(input.category())
                .sourceProvider(getSourceProvider(input.userId()))
                .build();

        Event createdEvent = eventDAO.create(event);

        return new EventDTOResponse(createdEvent);
    }

    @Override
    public EventDTOResponse updateById(int id, EventDTORequest input) {
        Event event = eventDAO.readById(id);

        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        String primaryImageUrl = input.primaryImageUrl();
        if (input.useDefaultImage()) {
            primaryImageUrl = "resources/images/default.jpg";
        }
        event.setSourceProvider(SourceProvider.ADMIN);

        event.setTitle(input.title());
        event.setDescription(input.description());
        event.setPrice(input.price());
        event.setFree(input.free());
        event.setLocation(address);
        event.setLongitude(coordinates.longitude());
        event.setLatitude(coordinates.latitude());
        event.setStartTime(input.startTime());
        event.setStartDate(input.startDate());
        event.setUrl(input.url());
        event.setImageUrl(primaryImageUrl);
        event.setCategory(input.category());

        Event updatedEvent = eventDAO.update(event);
        return new EventDTOResponse(updatedEvent);
    }

    @Override
    public EventDTOResponse getById(int id) {
        Event event = eventDAO.readById(id);
        return new EventDTOResponse(event);
    }

    @Override
    public List<EventDTOResponse> getAll() {
        List<Event> events = eventDAO.readAll();
        List<EventDTOResponse> eventDTOs = new ArrayList<>();
        if (events != null && !events.isEmpty()) {
            for (Event event : events) {
                eventDTOs.add(new EventDTOResponse(event));
            }
        }
        return eventDTOs;
    }

    @Override
    public boolean deleteById(int id) {
        Event event = eventDAO.readById(id);
        return eventDAO.delete(event);
    }

    public void persistTmEvents() {
        counter = 0;
        int maxCount = 5;
        List<Event> events = new ArrayList<>();
        List<TicketMasterDTO> ticketMasterDTOs = apiReader.getApiAsTmDTO();

        for (TicketMasterDTO ticketMasterDTO : ticketMasterDTOs) {
            if (ticketMasterDTO != null && ticketMasterDTO.embedded() != null && ticketMasterDTO.embedded().events() != null) {
                events.addAll(converter.ticketMasterDtoToEvent(ticketMasterDTO));
            }
        }

        for (Event event : events) {
            event.setSourceProvider(SourceProvider.API_TICKETMASTER);
            String description = checkForDescription(event, maxCount);
            if (description != null) {
                event.addDescription(description);
            }
            eventDAO.create(event);
        }
    }

    private String checkForDescription(Event event, int maxCount) {
        String description;
        if (event.getDescription() == null || event.getDescription().isEmpty()) {
            if (counter < maxCount) {
                counter++;
                description = apiReader.geminiDescriptionCreator(event);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                description = DefaultDescription.generateDefaultDescription(event);
            }
        } else {
            description = event.getDescription();
        }
        return description;
    }

    private SourceProvider getSourceProvider(int userId) {
        SourceProvider sourceProvider = null;
        UserDTOResponse userDTOResponse = userService.getById(userId);
        if (userDTOResponse.userRole() == UserRole.ADMIN) {
            sourceProvider = SourceProvider.ADMIN;
        } else if (userDTOResponse.userRole() == UserRole.ORGANIZER) {
            sourceProvider = SourceProvider.ORGANIZER;
        }
        return sourceProvider;
    }
}
