package app.utils;

import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.Status;
import app.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.LinkedHashMap;
import java.util.Map;

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
                em.createNativeQuery("TRUNCATE TABLE user RESTART IDENTITY CASCADE").executeUpdate();

                em.persist(admin);
                em.persist(admin2);
                em.persist(admin3);
                em.persist(admin4);
                em.persist(admin5);

                em.persist(attendee);
                em.persist(attendee2);
                em.persist(attendee3);
                em.persist(attendee4);
                em.persist(attendee5);

                em.persist(organizer);
                em.persist(organizer2);
                em.persist(organizer3);
                em.persist(organizer4);
                em.persist(organizer5);

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
}
