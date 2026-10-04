package app.utils;

import app.entities.Event;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DefaultDescription {
    public static String generateDefaultDescription(Event event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE 'the' d. MMMM yyyy", Locale.ENGLISH);
        String startDate = event.getStartDate().format(dateFormatter);

        return event.getTitle() + " will take place at "
                + event.getLocation().getAddress() + " on "
                + startDate + " at "
                + event.getStartTime()+"<!-- default-message -->";
    }
}
