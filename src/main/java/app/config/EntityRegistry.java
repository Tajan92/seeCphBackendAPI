package app.config;

import app.entities.Advert;
import app.entities.Event;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Admin.class);
        configuration.addAnnotatedClass(Attendee.class);
        configuration.addAnnotatedClass(Organizer.class);
        configuration.addAnnotatedClass(Event.class);
        configuration.addAnnotatedClass(Advert.class);
    }
}