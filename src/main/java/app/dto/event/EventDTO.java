package app.dto.event;

import app.entities.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTO(String title, String description, double price, String city, String postalCode, String address, LocalTime startTime, LocalDate startDate) {

    public EventDTO (Event event) {
        this(event.getTitle(), event.getDescription(), event.getPrice(), event.getLocation().getCity(), event.getLocation().getPostalCode(), event.getLocation().getAddress(), event.getStartTime(), event.getStartDate());
    }
}
