package app.dao;

import app.entities.ImageUrl;
import jakarta.persistence.EntityManagerFactory;

public class ImageUrlDAO extends GenericDAO<ImageUrl> {
    public ImageUrlDAO(EntityManagerFactory emf) {
        super(emf, ImageUrl.class, ImageUrl.class.getSimpleName());
    }
}
