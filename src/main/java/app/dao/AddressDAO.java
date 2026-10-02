package app.dao;

import app.entities.Address;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

public class AddressDAO extends GenericDAO<Address> {
    public AddressDAO(EntityManagerFactory emf) {
        super(emf, Address.class, Address.class.getSimpleName());
    }

    public Address readAdressByPostalCodeAndAddress(String postalCode, String address) {
        if (postalCode == null && address == null) {
            throw new DatabaseIdException("Postal code and address is required");
        }
        String jpql = "SELECT a FROM Address a WHERE a.address = :address AND a.postalCode = :postalCode";
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(jpql, Address.class)
                    .setParameter("address", address)
                    .setParameter("postalCode", postalCode)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } catch (PersistenceException e) {
            throw new DatabaseException("Reading " + Address.class.getSimpleName() + " failed", e);
        }
    }
}
