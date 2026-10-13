package app.mapper;

import app.config.AppConstants;
import app.dto.ticketMaster.*;
import app.entities.Address;
import app.entities.Event;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.service.AddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class TicketMasterConverter {
    private final AddressService addressService;

    public List<Event> ticketMasterDtoToEvent(TicketMasterDTO ticketMasterDTO) {
        List<Event> events = new ArrayList<>();
        for (TmEvent tmEvent : ticketMasterDTO.embedded().events()) {
            try {
                events.add(convert(tmEvent));
            } catch (Exception e) {
                log.warn("Ticket Master event with {}: {} not added - {}", tmEvent.id(), tmEvent.name(), e.getMessage());
            }
        }
        return events;
    }

    private Event convert(TmEvent tmEvent) {
        TmVenue venue = firstLocation(tmEvent);
        TmVenue.TmVenueLocation coords = venue != null ? venue.location() : null;

        return Event.builder()
                .apiEventId(tmEvent.id())
                .sourceEventId(tmEvent.id())
                .sourceProvider(SourceProvider.API_TICKETMASTER)
                .title(tmEvent.name())
                .description(tmEvent.info())
                .price(checkPrice(tmEvent))
                .free(false) // Ticketmaster is never free
                .startDate(parseDate(tmEvent))
                .startTime(parseTime(tmEvent))
                .location(findAddress(venue))
                .latitude(coords != null ? coords.latitude() : null)
                .longitude(coords != null ? coords.longitude() : null)
                .category(findEventCategory(tmEvent))
                .imageUrl(findImageUrl(tmEvent))
                .url(tmEvent.url())
                .build();
    }

    private TmVenue firstLocation(TmEvent tmEvent) {
        if (tmEvent.embedded() == null || tmEvent.embedded().venues() == null
                || tmEvent.embedded().venues().isEmpty()) {
            return null;
        }
        return tmEvent.embedded().venues().getFirst();
    }

    private LocalDate parseDate(TmEvent tmEvent) {
        if (tmEvent.dates() == null || tmEvent.dates().start() == null
                || tmEvent.dates().start().localDate() == null) {
            return null;
        }
        return LocalDate.parse(tmEvent.dates().start().localDate());
    }

    private LocalTime parseTime(TmEvent tmEvent) {
        if (tmEvent.dates() == null || tmEvent.dates().start() == null
                || tmEvent.dates().start().localTime() == null) {
            return null;
        }
        return LocalTime.parse(tmEvent.dates().start().localTime());
    }

    private Address findAddress(TmVenue venue) {
        if (venue == null || venue.address() == null || venue.address().address() == null) {
            return null;
        }
        String city = venue.city() != null ? venue.city().name() : null;
        return addressService.createOrFindAddress(venue.postalCode(), city, venue.address().address());
    }

    private String findImageUrl(TmEvent tmEvent) {
        if (tmEvent.images() == null || tmEvent.images().isEmpty()) {
            return AppConstants.DEFAULT_IMAGE_URL; // Default local image
        }
        return tmEvent.images().stream()
                .map(TmEvent.TmImage::url)
                .filter(Objects::nonNull)
                .filter(url -> url.endsWith("_SOURCE"))
                .findFirst()
                .orElseGet(() -> tmEvent.images().getFirst().url());
    }

    private Double checkPrice(TmEvent event) {
        Double price = null;
        if (event.priceRanges() != null) {
            for (TmPriceRange priceRange : event.priceRanges()) {
                if (priceRange != null) {
                    price = priceRange.min();
                }
            }
        }
        return price;
    }

    private EventCategory findEventCategory(TmEvent event) {
        if (event.classifications() == null) {
            return EventCategory.OTHERS;
        }
        EventCategory eventCategory = event.classifications().stream()
                .filter(TmClassification::primary)
                .findFirst()
                .map(TmClassification::genre)
                .map(TmClassification.TmGenre::name)
                .orElse(EventCategory.OTHERS);

        if (eventCategory == EventCategory.OTHERS) {
            eventCategory = event.classifications().stream()
                    .filter(TmClassification::primary)
                    .findFirst()
                    .map(TmClassification::subGenre)
                    .map(TmClassification.TmSubGenre::name)
                    .orElse(EventCategory.OTHERS);
        }
        return eventCategory;
    }
}
