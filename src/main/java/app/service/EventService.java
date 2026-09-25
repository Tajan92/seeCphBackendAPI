package app.service;

import app.dao.EventDAO;
import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.dto.ticketMaster.TicketMasterDTO;
import app.entities.Address;
import app.entities.Event;
import app.utils.APIReader;
import app.utils.GeoUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class EventService {
    private EventDAO eventDAO;
    private APIReader apiReader;
    private TicketMasterConverter converter;

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
        this.apiReader = new APIReader();
        this.converter = new TicketMasterConverter();
    }

//apiReader.getApiAsTmDTO("https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&page=$&apikey="+System.getenv("API_KEY"));

    public List<Event> persistTmEvents(List<TicketMasterDTO> ticketMasterDTOs) {
        List<Event> events = new ArrayList<>();

        for (TicketMasterDTO ticketMasterDTO : ticketMasterDTOs) {
            if (ticketMasterDTO != null && ticketMasterDTO.embedded() != null && ticketMasterDTO.embedded().events() != null) {
                events.addAll(converter.ticketMasterDtoToEvent(ticketMasterDTO));
            }
        }

        String description = null;
        for (Event event : events) {
            description = checkForDescription(event);
            if (description != null) {
                event.addDescription(description);
            }
            eventDAO.create(event);
        }
        return events;
    }

    public String checkForDescription(Event event) {
        String description = null;
        if (event.getDescription() == null || event.getDescription().isEmpty()) {
            description = apiReader.geminiDescriptionCreator(event.getTitle(), event.getLocation().getAddress(), event.getUrl());
        }
        return description;
    }

    public EventDTOResponse createEvent(EventDTORequest input) {
        Address address = Address.builder()
                .postalCode(input.postalCode())
                .city(input.city())
                .address(input.address())
                .build();

        String primaryImageUrl = input.primaryImageUrl();
        if (input.useDefaultImage()) {
            primaryImageUrl = "resources/images/default.jpg";
        }
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        Event event = Event.builder() //Todo: logic for sourceProvider
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
                .build();

        Event createdEvent = eventDAO.create(event);

        return new EventDTOResponse(createdEvent);
    }

    public EventDTOResponse updateEventById(int id, EventDTORequest input) {
        Event event = eventDAO.readById(id);
        // TODO: Setters fra input til event
        Event updatedEvent = eventDAO.update(event);
        return new EventDTOResponse(updatedEvent);
    }

    public EventDTOResponse getEventById(int id) {
        Event event = eventDAO.readById(id);
        return new EventDTOResponse(event);
    }

    public List<EventDTOResponse> getAllEvents() {
        Set<Event> events = eventDAO.readAll();
        List<EventDTOResponse> eventDTOs = new ArrayList<>();
        if (events != null && !events.isEmpty()) {
            for (Event event : events) {
                eventDTOs.add(new EventDTOResponse(event));
            }
        }
        return eventDTOs;
    }

    public boolean deleteEventById(int id) {
        Event event = eventDAO.readById(id);
        return eventDAO.delete(event);
    }
}
