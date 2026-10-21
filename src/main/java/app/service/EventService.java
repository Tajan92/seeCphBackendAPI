package app.service;

import app.config.AppConstants;
import app.dao.EventDAO;
import app.dao.UserDAO;
import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.user.UserDTOResponse;
import app.entities.Address;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.enums.UserRole;
import app.exceptions.ConflictException;
import app.exceptions.SecurityValidationException;
import app.mapper.EventConverter;
import app.mapper.TicketMasterConverter;
import app.utils.APIReader;
import app.utils.DefaultDescription;
import app.utils.GeoUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class EventService {
    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    private final AddressService addressService;
    private final APIReader apiReader;
    private final TicketMasterConverter converter;
    private final EventConverter eventConverter;
    private int counter;
    private final AtomicBoolean syncRunning = new AtomicBoolean(false);

    public EventService(EventDAO eventDAO, AddressService addressService, UserDAO userDAO) {
        this.eventDAO = eventDAO;
        this.addressService = addressService;
        this.apiReader = new APIReader();
        this.converter = new TicketMasterConverter();
        this.eventConverter = new EventConverter();
        this.userDAO = userDAO;
    }

    public EventDTOResponse create(EventDTORequest input, int userId) {
        User user = userDAO.readById(userId);
        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        Event event = Event.builder()
                .title(input.title())
                .description(input.description())
                .price(input.price())
                .free(input.free())
                .location(address)
                .longitude(coordinates != null ? coordinates.longitude() : null)
                .latitude(coordinates != null ? coordinates.latitude() : null)
                .startTime(input.startTime())
                .startDate(input.startDate())
                .url(input.url())
                .imageUrl(resolveImageUrl(input))
                .category(input.category())
                .build();

        if (user instanceof Organizer organizer) {
            event.setOrganizer(organizer);
            event.setSourceProvider(SourceProvider.ORGANIZER);
        } else if (user instanceof Admin admin) {
            event.setAdmin(admin);
            event.setSourceProvider(SourceProvider.ADMIN);
        }

        Event createdEvent = eventDAO.create(event);
        return eventConverter.convertEntityToDTO(createdEvent);
    }

    public EventDTOResponse updateById(int id, EventDTORequest input, UserDTOResponse currentUser) {
        Event event = eventDAO.readById(id);
        requireOwnerOrAdmin(event, currentUser);

        Address address = addressService.createOrFindAddress(input.postalCode(), input.city(), input.address());
        GeoUtil.Coordinates coordinates = GeoUtil.findCoordinates(address);

        event.setTitle(input.title());
        event.setDescription(input.description());
        event.setPrice(input.price());
        event.setFree(input.free());
        event.setLocation(address);
        event.setLongitude(coordinates != null ? coordinates.longitude() : null);
        event.setLatitude(coordinates != null ? coordinates.latitude() : null);
        event.setStartTime(input.startTime());
        event.setStartDate(input.startDate());
        event.setUrl(input.url());
        event.setImageUrl(resolveImageUrl(input));
        event.setCategory(input.category());

        Event updatedEvent = eventDAO.update(event);
        return eventConverter.convertEntityToDTO(updatedEvent);
    }

    public EventDTOResponse getById(int id) {
        return eventConverter.convertEntityToDTO(eventDAO.readById(id));
    }

    public List<EventDTOResponse> getByUserId(int id) {
        List<Event> event = eventDAO.readEventsByUserId(id);
        return eventConverter.convertEntityListToDTOList(event);
    }

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

    public void deleteById(int id, UserDTOResponse currentUser) {
        Event event = eventDAO.readById(id);
        requireOwnerOrAdmin(event, currentUser);
        eventDAO.delete(event);
    }

    public void syncEvents() {


        if (!syncRunning.compareAndSet(false, true)) {
            throw new ConflictException("Ticketmaster sync is already running");
        }
        try (ExecutorService executorService = Executors.newFixedThreadPool(1)){ // NB! only 1 thread
            // Fetch from API
            Future<List<Event>> futures = executorService.submit(this::fetchTmEvents);

            // Save to database
            saveTmEventsToDB(futures.get());

            // Delete expired events
            runDeleteExpiredEventsSync();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            syncRunning.set(false);
        }
    }

    private void runDeleteExpiredEventsSync(){
        List<Event> events = eventDAO.readAll();
        for (Event event : events) {
            if (event.getStartDate().isBefore(LocalDate.now().minusDays(3))) {
                eventDAO.delete(event);
            }
        }
    }

    private List<Event> fetchTmEvents(){
        List<Event> events = new ArrayList<>();
        for (TicketMasterDTO dto : apiReader.getApiAsTmDTO()) {
            if (dto != null && dto.embedded() != null && dto.embedded().events() != null) {
                events.addAll(converter.ticketMasterDtoToEvent(dto));
            }
        }
        return events;
    }

    private void saveTmEventsToDB(List<Event> events) {
        Map<String, Event> existingTmEvents = eventDAO.readAllTmEvents().stream()
                .filter(e -> e.getApiEventId() != null)
                .collect(Collectors.toMap(Event::getApiEventId, e -> e, (a, b) -> a));

        counter = 0;
        int maxCount = 15;
        int created = 0;
        int updated = 0;
        int failed = 0;

        for (Event event : events) {
            try {
                Address a = event.getLocation();
                if (a != null) {
                    event.setLocation(addressService.createOrFindAddress(a.getPostalCode(), a.getCity(), a.getAddress()));
                }
                Event existing = existingTmEvents.get(event.getApiEventId());
                if (existing == null) {
                    event.setDescription(checkForDescription(event, maxCount));
                    eventDAO.create(event);
                    existingTmEvents.put(event.getApiEventId(), event); // avoid duplicates within one sync
                    created++;
                } else {
                    copyTicketmasterFields(event, existing);
                    existing.setDescription(checkForDescription(existing, maxCount));
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

    private void requireOwnerOrAdmin(Event event, UserDTOResponse currentUser) {
        if (currentUser.userRole() == UserRole.ADMIN) return;

        boolean isOwner = event.getOrganizer() != null && Objects.equals(event.getOrganizer().getUserId(), currentUser.id());
        if (!isOwner) {
            throw new SecurityValidationException(403, "You can only change your own events");
        }
    }

    private String resolveImageUrl(EventDTORequest input) {
        return input.useDefaultImage() ? AppConstants.DEFAULT_IMAGE_URL : input.primaryImageUrl();
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
}
