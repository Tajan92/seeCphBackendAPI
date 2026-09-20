package app.service;

import app.dao.EventDAO;
import app.dto.ticketMaster.TicketMasterDTO;
import app.entities.Event;
import app.utils.APIReader;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    private EventDAO eventDAO;
    private APIReader apiReader;
    private TicketMasterConverter converter;

    public EventService(EntityManagerFactory emf) {
        this.eventDAO = new EventDAO(emf);
        this.apiReader = new APIReader();
        this.converter = new TicketMasterConverter();
    }

    public List<Event> persistEvents() {
        List<TicketMasterDTO> ticketMasterDTOs = apiReader.getApiAsTmDTO("https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&page=$&apikey=" + System.getenv("API_KEY"));
        List<Event> events = new ArrayList<>();
        for (TicketMasterDTO ticketMasterDTO : ticketMasterDTOs) {
            if (ticketMasterDTO != null && ticketMasterDTO.embedded() != null && ticketMasterDTO.embedded().events() != null) {
                events.addAll(converter.ticketMasterDtoToEvent(ticketMasterDTO));
            }
        }
        for (Event event : events) {
            eventDAO.create(event);
        }
        return events;
    }
}
