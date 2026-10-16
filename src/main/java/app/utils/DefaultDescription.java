package app.utils;

import app.entities.Event;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DefaultDescription {
    public static String generateDefaultDescription(Event event) {
        String address = event.getLocation() != null && event.getLocation().getAddress() != null ? "at "+event.getLocation().getAddress() : "in Copenhagen";
        String title = event.getTitle() != null ? event.getTitle() : "This event";
        if (event.getStartDate() == null || event.getStartTime() == null) {
            return title + " will take place "
                    + address +"<!-- default-message -->";
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE 'the' d. MMMM yyyy", Locale.ENGLISH);
        String startDate = event.getStartDate().format(dateFormatter);
        return title + " will take place "
                + address + " on "
                + startDate + " at "
                + event.getStartTime()+" <!-- default-message -->";
    }
}
