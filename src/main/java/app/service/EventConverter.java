package app.service;

import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.ticketMaster.TmClassification;
import app.dto.ticketMaster.TmEvent;
import app.dto.ticketMaster.TmPriceRange;
import app.entities.Address;
import app.entities.Event;
import app.entities.ImageUrl;
import app.enums.EventCategory;
import app.enums.EventSubCategory;
import app.enums.SourceProvider;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EventConverter {

    public Event ticketMasterDtoToEvent(TicketMasterDTO ticketMasterDTO) {
        Event eventBuild = new Event();
        for (TmEvent event : ticketMasterDTO.embedded().events()) {
            Address address = Address.builder()
                    .address(event.embedded().venues().getFirst().address().address())
                    .city(event.embedded().venues().getFirst().city().name())
                    .postalCode(event.embedded().venues().getFirst().postalCode())
                    .build();

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
            Double price = null;
            for (TmPriceRange priceRange : event.priceRanges()) {
                if (priceRange != null) {
                    price = priceRange.min();
                }
            }
            String category = null;
            String subCategory = null;
            for (TmClassification classification : event.classifications()) {
                if (classification.primary()) {
                    category = classification.genre().name();
                    subCategory = classification.subGenre().name();
                }
            }
            EventCategory eventCategory = null;
            if (category != null) {
                eventCategory = categoryPicker(category);
            }
            EventSubCategory eventSubCategory = null;
            if (subCategory != null) {
                eventSubCategory = subCategoryPicker(subCategory);
            }

            eventBuild = Event.builder()
                    .sourceEventId(event.id())
                    .sourceProvider(SourceProvider.TICKETMASTER)
                    .title(event.name())
                    .description(event.info())
                    .price(price)
                    .free(false) //Ticket Master never free
                    .startDate(LocalDate.parse(event.dates().start().localDate()))
                    .startTime(LocalTime.parse(event.dates().start().localTime()))
                    .location(address)
                    .longitude(event.embedded().venues().getFirst().location().latitude())
                    .latitude(event.embedded().venues().getFirst().location().longitude())
                    .category(eventCategory)
                    .subCategory(eventSubCategory)
                    .images(imageUrls)
                    .build();
        }
        return eventBuild;
    }

    private EventCategory categoryPicker(String category) {
        EventCategory eventCategory;
        switch (category.toUpperCase()) {
            case "MUSIC" -> eventCategory = EventCategory.MUSIC;
            case "SPORTS" -> eventCategory = EventCategory.SPORTS;
            case "ARTS" -> eventCategory = EventCategory.ARTS;
            case "COMEDY" -> eventCategory = EventCategory.COMEDY;
            case "FAMILY" -> eventCategory = EventCategory.FAMILY;
            case "GAMING" -> eventCategory = EventCategory.GAMING;
            default -> eventCategory = EventCategory.OTHER;
        }
        return eventCategory;
    }

    private EventSubCategory subCategoryPicker(String subCategory) {
        EventSubCategory eventSubCategory;
        switch (subCategory.toUpperCase()) {
            case "ROCK" -> eventSubCategory = EventSubCategory.ROCK;
            case "POP" -> eventSubCategory = EventSubCategory.POP;
            case "HIPHOP/RAP" -> eventSubCategory = EventSubCategory.HIPHOP_RAP;
            case "ELECTRONIC" -> eventSubCategory = EventSubCategory.ELECTRONIC;
            case "COUNTRY" -> eventSubCategory = EventSubCategory.COUNTRY;
            case "ALTERNATIVE", "BALLADS/ROMANTIC", "BLUES", "CHANSON/FRANCAISE" ->
                    eventSubCategory = EventSubCategory.OTHER_MUSIC;
            case "ATHLETIC/RACES" -> eventSubCategory = EventSubCategory.ATHLETIC_RACES;
            case "BADMINTON" -> eventSubCategory = EventSubCategory.BADMINTON;
            case "BOXING" -> eventSubCategory = EventSubCategory.BOXING;
            case "CYCLING" -> eventSubCategory = EventSubCategory.CYCLING;
            case "ESPORTS" -> eventSubCategory = EventSubCategory.ESPORTS;
            case "MARTIAL ARTS", "GYMNASTICS", "SKI/JUMPING", "SKIING" -> eventSubCategory = EventSubCategory.EXTREME;
            case "SOCCER" -> eventSubCategory = EventSubCategory.FOOTBALL;
            case "GOLF" -> eventSubCategory = EventSubCategory.GOLF;
            case "HANDBALL" -> eventSubCategory = EventSubCategory.HANDBALL;
            case "RACE", "MOTORSPORTS/RACING" -> eventSubCategory = EventSubCategory.MOTORSPORTS_RACING;
            case "WRESTLING" -> eventSubCategory = EventSubCategory.WRESTLING;
            case "FOOD AND DRINK" -> eventSubCategory = EventSubCategory.FOOD_AND_DRINK;
            case "HEALTH WELLNESS" -> eventSubCategory = EventSubCategory.HEALTH;
            default -> eventSubCategory = EventSubCategory.OTHER;
        }
        return eventSubCategory;
    }
}
