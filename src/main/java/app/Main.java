package app;

import app.config.HibernateConfig;
import app.daos.AdvertDAO;
import app.daos.EventDAO;
import app.daos.UserDAO;
import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.EventCategory;
import app.enums.Status;
import app.enums.UserRole;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        UserDAO userDAO = new UserDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        AdvertDAO advertDAO = new AdvertDAO(emf);

        createUsers(userDAO);
        createEvent(eventDAO);
        createAdvert(advertDAO);

        emf.close();
    }

    public static void createEvent(EventDAO eventDAO) {
        Event event = Event.builder()
                .title("Lukas Graham")
                .description("Lukas performing at Parken")
                .price(350.00)
                .isFree(false)
                .location("Parken 1, København")
                .latitude(55.702724)
                .longitude(12.571566)
                .startTime(LocalTime.of(20, 30))
                .endTime(LocalTime.of(22, 00))
                .startDates(Collections.singleton(LocalDate.of(2026, 10, 10)))
                .endDates(Collections.singleton(LocalDate.of(2026, 10, 10)))
                .categories(Collections.singleton(EventCategory.MUSIC)).build();

        eventDAO.create(event);


    }

    public static void createAdvert(AdvertDAO advertDAO) {
        Advert advert = Advert.builder()
                .placement("Rolling banner header")
                .price(500.00)
                .startDate(LocalDate.of(2026, 11, 15))
                .endDate(LocalDate.of(2026, 11, 22))
                .status(true).build();

        advertDAO.create(advert);
    }

    public static void createUsers(UserDAO userDAO) {
        User user = Admin.builder().name("John").email("john@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ADMIN).build();
        userDAO.create(user);

        User user2 = Attendee.builder().name("Bo").email("bo@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ATTENDEE).build();
        userDAO.create(user2);

        User user3 = Organizer.builder().organizerName("Nike").accountStatus(Status.PENDING).name("Nike").email("nike@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ORGANIZER).build();
        userDAO.create(user3);
    }
}

