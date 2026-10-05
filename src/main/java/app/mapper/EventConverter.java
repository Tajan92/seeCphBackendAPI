package app.mapper;

import app.dto.event.EventDTORequest;
import app.dto.event.EventDTOResponse;
import app.entities.Address;
import app.entities.Event;

import java.util.ArrayList;
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
        return new EventDTOResponse(event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getPrice(),
                event.isFree(),
                event.getLocation().getCity(),
                event.getLocation().getPostalCode(),
                event.getLocation().getAddress(),
                event.getStartTime(),
                event.getStartDate(),
                event.getUrl(),
                event.getImageUrl(),
                event.getCategory().getLabel());
    }

    public List<EventDTOResponse> convertEntityListToDTOList (List<Event> events) {
        List<EventDTOResponse> dtos = new ArrayList<>();
        for (Event event : events) {
            dtos.add(convertEntityToDTO(event));
        }
        return dtos;
    }
}
