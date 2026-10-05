package app.mapper;

import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.ticketMaster.TmClassification;
import app.dto.ticketMaster.TmEvent;
import app.dto.ticketMaster.TmPriceRange;
import app.entities.Event;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import app.service.AddressService;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TicketMasterConverter {
    private final AddressService addressService;

    public List<Event> ticketMasterDtoToEvent(TicketMasterDTO ticketMasterDTO) {
        List<Event> events = new ArrayList<>();
        Event eventBuild = new Event();
        for (TmEvent eventDTO : ticketMasterDTO.embedded().events()) {
            String url = null;
            if (!eventDTO.images().isEmpty()) {
                url = eventDTO.images().stream()
                        .filter(image -> image.url().endsWith("_SOURCE"))
                        .findFirst()
                        .map(TmEvent.TmImage::url)
                        .orElseGet(() -> eventDTO.images().getFirst().url());
            }
            eventBuild = Event.builder()
                    .sourceEventId(eventDTO.id())
                    .sourceProvider(SourceProvider.API_TICKETMASTER)
                    .title(eventDTO.name())
                    .description(eventDTO.info())
                    .price(checkPrice(eventDTO))
                    .free(false) //Ticket Master never free
                    .startDate(eventDTO.dates().start().localDate() != null ? LocalDate.parse(eventDTO.dates().start().localDate()) : null)
                    .startTime(eventDTO.dates().start().localTime() != null ? LocalTime.parse(eventDTO.dates().start().localTime()) : null)
                    .location(addressService.createOrFindAddress(eventDTO.embedded().venues().getFirst().postalCode(), eventDTO.embedded().venues().getFirst().city().name(), eventDTO.embedded().venues().getFirst().address().address()))
                    .longitude(eventDTO.embedded().venues().getFirst().location().longitude())
                    .latitude(eventDTO.embedded().venues().getFirst().location().latitude())
                    .category(findEventCategory(eventDTO))
                    .imageUrl(url)
                    .url(eventDTO.url())
                    .apiEventId(eventDTO.id())
                    .build();
            events.add(eventBuild);
        }
        return events;
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
