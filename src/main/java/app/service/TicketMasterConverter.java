package app.service;

import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.ticketMaster.TmClassification;
import app.dto.ticketMaster.TmEvent;
import app.dto.ticketMaster.TmPriceRange;
import app.entities.Address;
import app.entities.Event;
import app.entities.ImageUrl;
import app.enums.EventCategory;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TicketMasterConverter {
    public List<Event> ticketMasterDtoToEvent(TicketMasterDTO ticketMasterDTO) {
       List<Event> events = new ArrayList<>();
        Event eventBuild = new Event();
        for (TmEvent event : ticketMasterDTO.embedded().events()) {
            eventBuild = Event.builder()
                    .sourceEventId(event.id())
                    .sourceProvider("Ticket Master")
                    .title(event.name())
                    .description(event.info())
                    .price(chechPrice(event))
                    .free(false) //Ticket Master never free
                    .startDate(event.dates().start().localDate() != null ? LocalDate.parse(event.dates().start().localDate()) : null)
                    .startTime(event.dates().start().localTime() != null ? LocalTime.parse(event.dates().start().localTime()) : null)                    .location(createAddress(event))
                    .longitude(event.embedded().venues().getFirst().location().latitude())
                    .latitude(event.embedded().venues().getFirst().location().longitude())
                    .category(findEventCategory(event))
                    .images(createImageUrls(event))
                    .url(event.url())
                    .build();
            events.add(eventBuild);
        }
        return events;
    }

    private Address createAddress(TmEvent event) {
        return Address.builder()
                .address(event.embedded().venues().getFirst().address().address())
                .city(event.embedded().venues().getFirst().city().name())
                .postalCode(event.embedded().venues().getFirst().postalCode())
                .build();
    }

    private List<ImageUrl> createImageUrls(TmEvent event) {
        List<ImageUrl> imageUrls = new ArrayList<>();

        for (TmEvent.TmImage image : event.images()) {
            ImageUrl imageUrl = ImageUrl.builder()
                    .url(image.url())
                    .ratio(image.ratio())
                    .height(image.height())
                    .width(image.width())
                    .build();
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    private Double chechPrice(TmEvent event) {
        Double price = null;
        if (event.priceRanges() != null){
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

        if(eventCategory == EventCategory.OTHERS) {
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
