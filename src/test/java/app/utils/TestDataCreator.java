package app.utils;

import app.dto.ticketMaster.TicketMasterDTO;
import app.entities.Address;
import app.entities.Advert;
import app.entities.Event;
import app.entities.ImageUrl;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.*;
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

            Advert advert = Advert.builder().addPlacement(AddPlacement.FRONTPAGEHEADER).price(205.00).startDate(LocalDate.now().plusDays(10)).endDate(LocalDate.now().plusDays(20)).build();
            Advert advert2 = Advert.builder().addPlacement(AddPlacement.FRONTPAGEHIGHLIGHT).price(500.00).startDate(LocalDate.now().minusDays(5)).endDate(LocalDate.now().plusDays(5)).build();
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

            Address address = Address.builder()
                    .postalCode("2100")
                    .city("København")
                    .address("Per Henrik Lings Allé 2")
                    .build();

            Event event = Event.builder()
                    .title("Lukas Graham")
                    .description("Enjoy Lukas Graham at Parken")
                    .category(EventCategory.MUSIC)
                    .startTime(LocalTime.of(19, 30))
                    .startDate(LocalDate.of(2026, 11, 15))
                    .price(500.00)
                    .location(address)
                    .build();

            Address address2 = Address.builder()
                    .postalCode("1601")
                    .city("København V")
                    .address("Cirkusbygningen")
                    .build();

            Event event2 = Event.builder()
                    .title("Stand-up Comedy Night")
                    .description("An evening of laughs with top Danish comedians")
                    .category(EventCategory.COMEDY)
                    .startTime(LocalTime.of(20, 0))
                    .startDate(LocalDate.of(2026, 12, 5))
                    .price(250.00)
                    .location(address2)
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

    public static Map<String, Address> createAddresses(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Address address1 = Address.builder()
                    .postalCode("1050")
                    .city("København K")
                    .address("Kongens Nytorv 1")
                    .build();

            Address address2 = Address.builder()
                    .postalCode("2200")
                    .city("København N")
                    .address("Ravnsborggade 8")
                    .build();

            try {
                em.createNativeQuery("TRUNCATE TABLE address RESTART IDENTITY CASCADE").executeUpdate();

                List<Address> addresses = List.of(
                        address1, address2
                );
                addresses.forEach(em::persist);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
            em.getTransaction().commit();

            Map<String, Address> addressMap = new LinkedHashMap<>();
            addressMap.put("address", address1);
            addressMap.put("address2", address2);

            return addressMap;
        }
    }

    public static Map<String, ImageUrl> createImageUrls(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            ImageUrl imageUrl = ImageUrl.builder()
                    .url("randomUrl.dk")
                    .ratio("16_9")
                    .height(205)
                    .width(115)
                    .build();

            ImageUrl imageUrl2 = ImageUrl.builder()
                    .url("randomUrl2.dk")
                    .ratio("3_2")
                    .height(305)
                    .width(203)
                    .build();

            try {
                em.createNativeQuery("TRUNCATE TABLE image_url RESTART IDENTITY CASCADE").executeUpdate();

                List<ImageUrl> imageUrls = List.of(
                        imageUrl, imageUrl2
                );
                imageUrls.forEach(em::persist);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
            em.getTransaction().commit();

            Map<String, ImageUrl> imageUrlMap = new LinkedHashMap<>();
            imageUrlMap.put("imageUrl", imageUrl);
            imageUrlMap.put("imageUrl2", imageUrl2);

            return imageUrlMap;
        }
    }

    public static Map<String, TicketMasterDTO> createTicketMasterDTOs () {
        Map<String, TicketMasterDTO> ticketMasterDTOMap = new LinkedHashMap<>();
        return null;
    }
}
