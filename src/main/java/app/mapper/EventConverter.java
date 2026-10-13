package app.mapper;

import app.dto.event.EventDTOResponse;
import app.dto.event.EventDTORequest;
import app.entities.Address;
import app.entities.Event;

import java.util.List;

public class EventConverter implements IConverter<Event, EventDTOResponse, EventDTORequest> {

    @Override
    public Event convertDTOToEntity(EventDTORequest dto) {
        Address address = Address.builder()
                .address(dto.address())
                .city(dto.city())
                .postalCode(dto.postalCode())
                .build();

        return Event.builder()
                .title(dto.title())
                .description(dto.description())
                .price(dto.price())
                .free(dto.free())
                .location(address)
                .category(dto.category())
                .startDate(dto.startDate())
                .startTime(dto.startTime())
                .url(dto.url())
                .imageUrl(dto.primaryImageUrl())
                .build();
    }

    @Override
    public EventDTOResponse convertEntityToDTO(Event event) {
        Address location = event.getLocation();
        return new EventDTOResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getPrice(),
                event.isFree(),
                location != null ? location.getCity() : null,
                location != null ? location.getPostalCode() : null,
                location != null ? location.getAddress() : null,
                event.getStartTime(),
                event.getStartDate(),
                event.getUrl(),
                event.getImageUrl(),
                event.getCategory() != null ? event.getCategory().name() : null);
    }

    public List<EventDTOResponse> convertEntityListToDTOList(List<Event> events) {
        return events.stream().map(this::convertEntityToDTO).toList();
    }
}