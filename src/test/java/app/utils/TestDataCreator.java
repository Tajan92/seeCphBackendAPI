package app.utils;

import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.AddPlacement;
import app.enums.EventCategory;
import app.enums.Status;
import app.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public final class TestDataCreator {

    public static Map<String, User> createUsers(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User admin = Admin.builder().name("John").email("john@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ADMIN).build();
            User admin2 = Admin.builder().name("Mette").email("mette@mail.dk").phone("23456789").password("23456789").userRole(UserRole.ADMIN).build();
            User admin3 = Admin.builder().name("Lars").email("lars@mail.dk").phone("34567890").password("34567890").userRole(UserRole.ADMIN).build();
            User admin4 = Admin.builder().name("Sofie").email("sofie@mail.dk").phone("45678901").password("45678901").userRole(UserRole.ADMIN).build();
            User admin5 = Admin.builder().name("Anders").email("anders@mail.dk").phone("56789012").password("56789012").userRole(UserRole.ADMIN).build();

            User attendee = Attendee.builder().name("Bo").email("bo@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ATTENDEE).build();
            User attendee2 = Attendee.builder().name("Freja").email("freja@mail.dk").phone("23456789").password("23456789").userRole(UserRole.ATTENDEE).build();
            User attendee3 = Attendee.builder().name("Emil").email("emil@mail.dk").phone("34567890").password("34567890").userRole(UserRole.ATTENDEE).build();
            User attendee4 = Attendee.builder().name("Ida").email("ida@mail.dk").phone("45678901").password("45678901").userRole(UserRole.ATTENDEE).build();
            User attendee5 = Attendee.builder().name("Magnus").email("magnus@mail.dk").phone("56789012").password("56789012").userRole(UserRole.ATTENDEE).build();

            User organizer = Organizer.builder().organizerName("Nike").accountStatus(Status.PENDING).name("Nike").email("nike@mail.dk").phone("12345678").password("12345678").userRole(UserRole.ORGANIZER).build();
            User organizer2 = Organizer.builder().organizerName("Adidas").accountStatus(Status.ACTIVE).name("Adidas").email("adidas@mail.dk").phone("23456789").password("23456789").userRole(UserRole.ORGANIZER).build();
            User organizer3 = Organizer.builder().organizerName("Puma").accountStatus(Status.PENDING).name("Puma").email("puma@mail.dk").phone("34567890").password("34567890").userRole(UserRole.ORGANIZER).build();
            User organizer4 = Organizer.builder().organizerName("Carlsberg").accountStatus(Status.REJECTED).name("Carlsberg").email("carlsberg@mail.dk").phone("45678901").password("45678901").userRole(UserRole.ORGANIZER).build();
            User organizer5 = Organizer.builder().organizerName("Lego").accountStatus(Status.ACTIVE).name("Lego").email("lego@mail.dk").phone("56789012").password("56789012").userRole(UserRole.ORGANIZER).build();

            try {
                em.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();

                List<User> users = List.of(
                        admin, admin2, admin3, admin4, admin5,
                        attendee, attendee2, attendee3, attendee4, attendee5,
                        organizer, organizer2, organizer3, organizer4, organizer5
                );
                users.forEach(em::persist);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
            em.getTransaction().commit();
            Map<String, User> users = new LinkedHashMap<>();
            users.put("admin", admin);
            users.put("admin2", admin2);
            users.put("admin3", admin3);
            users.put("admin4", admin4);
            users.put("admin5", admin5);
            users.put("attendee", attendee);
            users.put("attendee2", attendee2);
            users.put("attendee3", attendee3);
            users.put("attendee4", attendee4);
            users.put("attendee5", attendee5);
            users.put("organizer", organizer);
            users.put("organizer2", organizer2);
            users.put("organizer3", organizer3);
            users.put("organizer4", organizer4);
            users.put("organizer5", organizer5);

            return users;
        }
    }

    public static Map<String, Advert> createAdverts(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Advert advert = Advert.builder().addPlacement(AddPlacement.FRONTPAGEHEADER).price(205.00).startDate(LocalDate.now()).endDate(LocalDate.of(2026, 12, 12)).build();
            Advert advert2 = Advert.builder().addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT).price(500.00).startDate(LocalDate.of(2026, 11, 15)).endDate(LocalDate.of(2026, 11, 22)).build();
            try {
                em.createNativeQuery("TRUNCATE TABLE advert RESTART IDENTITY CASCADE").executeUpdate();

                List<Advert> adverts = List.of(
                        advert, advert2
                );
                adverts.forEach(em::persist);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }

            em.getTransaction().commit();

            Map<String, Advert> advertsMap = new LinkedHashMap<>();
            advertsMap.put("advert", advert);
            advertsMap.put("advert2", advert2);

            return advertsMap;
        }
    }

    public static Map<String, Event> createEvents(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Event event = Event.builder()
                    .title("Lukas Graham")
                    .description("Enjoy Lukas Graham at Parken")
                    .categories(Set.of(EventCategory.MUSIC))
                    .startTime(LocalTime.of(19,30))
                    .endTime(LocalTime.of(23,30))
                    .startDates(Set.of(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 22)))
                    .price(500.00)
                    .location("Per Henrik Lings Allé 2, 2100 København")
                    .build();

            Event event2 = Event.builder()
                    .title("Stand-up Comedy Night")
                    .description("An evening of laughs with top Danish comedians")
                    .categories(Set.of(EventCategory.COMEDY))
                    .startTime(LocalTime.of(20, 0))
                    .endTime(LocalTime.of(22, 0))
                    .startDates(Set.of(LocalDate.of(2026, 12, 5), LocalDate.of(2026, 12, 12)))
                    .price(250.00)
                    .location("Cirkusbygningen, 1601 København V")
                    .build();

            try {
                em.createNativeQuery("TRUNCATE TABLE event RESTART IDENTITY CASCADE").executeUpdate();

                List<Event> events = List.of(
                        event, event2
                );
                events.forEach(em::persist);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }

            em.getTransaction().commit();

            Map<String, Event> eventSet = new LinkedHashMap<>();
            eventSet.put("event", event);
            eventSet.put("event2", event2);

            return eventSet;
        }
    }
}
