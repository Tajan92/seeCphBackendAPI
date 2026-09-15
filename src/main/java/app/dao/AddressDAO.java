package app.dao;

import app.entities.Address;
import jakarta.persistence.EntityManagerFactory;

public class AddressDAO extends GenericDAO<Address> {
    public AddressDAO(EntityManagerFactory emf) {
        super(emf, Address.class, Address.class.getSimpleName());
    }
}
