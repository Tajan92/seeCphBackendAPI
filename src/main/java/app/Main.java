package app;

import app.config.ApplicationConfig;
import app.config.AdminCreator;
import app.config.HibernateConfig;
import app.dao.*;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        AdminCreator.createAdminIfMissing(new UserDAO(emf));
        ApplicationConfig applicationConfig = new ApplicationConfig(emf);
        applicationConfig.startServer(7070);
    }
}
