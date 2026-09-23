package app.service;

import app.dao.EventDAO;
import app.dto.event.EventDTO;
import app.dto.event.EventDTORequest;
import app.dto.ticketMaster.TicketMasterDTO;
import app.entities.Event;
import app.utils.APIReader;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public List<Event> persistEvents(List<TicketMasterDTO> ticketMasterDTOs) {
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

    public void createEvent(EventDTORequest input) {

    }

    public void updateEventById(int id, EventDTORequest input) {

    }

    public EventDTO getEventById(int id) {
        return null;
    }

    public List<EventDTO> getAllEvents() {
        return null;
    }

    public void deleteEventById() {

    }
}
