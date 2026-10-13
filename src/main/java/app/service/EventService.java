package app.service;

import app.config.AppConstants;
import app.dao.EventDAO;
import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.user.UserDTOResponse;
import app.entities.Address;
import app.entities.Event;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.enums.UserRole;
import app.mapper.EventConverter;
import app.mapper.TicketMasterConverter;
import app.utils.APIReader;
import app.utils.DefaultDescription;
import app.utils.GeoUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class EventService implements IService<EventDTORequest, EventDTOResponse> {
    private final EventDAO eventDAO;
    private final AddressService addressService;
    private final UserService userService;
    private final APIReader apiReader;
    private final TicketMasterConverter converter;
    private final EventConverter eventConverter;
    private int counter;

    public EventService(EventDAO eventDAO, AddressService addressService, UserService userService) {
        this.eventDAO = eventDAO;
        this.addressService = addressService;
        this.userService = userService;
        this.apiReader = new APIReader();
        this.converter = new TicketMasterConverter(addressService);
        this.eventConverter = new EventConverter();
    }

//apiReader.getApiAsTmDTO("https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&page=$&apikey="+System.getenv("API_KEY"));

    @Override
    public EventDTOResponse create(EventDTORequest input) {
        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());

        String primaryImageUrl = input.primaryImageUrl();
        if (input.useDefaultImage()) {
            primaryImageUrl = AppConstants.DEFAULT_IMAGE_URL;
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
        return eventConverter.convertEntityToDTO(createdEvent);
    }

    @Override
    public EventDTOResponse updateById(int id, EventDTORequest input) {
        Event event = eventDAO.readById(id);

        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        String primaryImageUrl = input.primaryImageUrl();
        if (input.useDefaultImage()) {
            primaryImageUrl = AppConstants.DEFAULT_IMAGE_URL;
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
        return eventConverter.convertEntityToDTO(updatedEvent);
    }

    @Override
    public EventDTOResponse getById(int id) {
        Event event = eventDAO.readById(id);
        return eventConverter.convertEntityToDTO(event);
    }

    @Override
    public List<EventDTOResponse> getAll() {
        List<Event> events = eventDAO.readAll();
        if (events == null) {
            return List.of();
        }
        return eventConverter.convertEntityListToDTOList(events);
    }

    public List<EventDTOResponse> getAllEventsBySearchAndFilter(String search, EventCategory category, LocalDate startDate, String postalCode, int page, int pageSize) {
        List<Event> events = eventDAO.searchAndFilterEvent(search, category, startDate, postalCode, page, pageSize);
        if (events == null) {
            return List.of();
        }
        return eventConverter.convertEntityListToDTOList(events);
    }

    public List<EventCategory> getAllActiveEventCategories() {
        return eventDAO.readDistinctCategoriesInDb();
    }

    @Override
    public boolean deleteById(int id) {
        Event event = eventDAO.readById(id);
        return eventDAO.delete(event);
    }

    public void syncTmEvents() {
        counter = 0;
        int maxCount = 15;

        Map<String, Event> existingTmEvents = eventDAO.readAllTmEvents().stream()
                .filter(e -> e.getApiEventId() != null)
                .collect(Collectors.toMap(Event::getApiEventId, e -> e, (a, b) -> a));

        List<Event> events = new ArrayList<>();
        for (TicketMasterDTO dto : apiReader.getApiAsTmDTO()) {
            if (dto != null && dto.embedded() != null && dto.embedded().events() != null) {
                events.addAll(converter.ticketMasterDtoToEvent(dto));
            }
        }

        int created = 0;
        int updated = 0;
        int failed = 0;

        for (Event event : events) {
            try {
                Event existing = existingTmEvents.get(event.getApiEventId());
                if (existing == null) {
                    event.addDescription(checkForDescription(event, maxCount));
                    eventDAO.create(event);
                    existingTmEvents.put(event.getApiEventId(), event); // avoid duplicates within one sync
                    created++;
                } else {
                    copyTicketmasterFields(event, existing);
                    existing.addDescription(checkForDescription(existing, maxCount));
                    eventDAO.update(existing);
                    updated++;
                }
            } catch (Exception e) {
                failed++;
                log.warn("Sync failed for Ticketmaster event {}: {}", event.getApiEventId(), e.getMessage());
            }
        }
        log.info("Ticketmaster sync done: {} created, {} updated, {} failed", created, updated, failed);
    }

    private void copyTicketmasterFields(Event from, Event to) {
        to.setTitle(from.getTitle());
        to.setPrice(from.getPrice());
        to.setStartDate(from.getStartDate());
        to.setStartTime(from.getStartTime());
        to.setLocation(from.getLocation());
        to.setLatitude(from.getLatitude());
        to.setLongitude(from.getLongitude());
        to.setCategory(from.getCategory());
        to.setImageUrl(from.getImageUrl());
        to.setUrl(from.getUrl());
        if (from.getDescription() != null && !from.getDescription().isBlank()) {
            to.setDescription(from.getDescription());
        }
    }

    private String checkForDescription(Event event, int maxCount) {
        String description;
        if (event.getDescription() == null || event.getDescription().isEmpty() || event.getDescription().contains("<!-- default-message -->") && !event.getDescription().contains("(Description created with AI)")) {
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
