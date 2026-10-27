package app.dao;

import app.entities.Address;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class AddressDAO extends GenericDAO<Address> {
    public AddressDAO(EntityManagerFactory emf) {
        super(emf, Address.class, Address.class.getSimpleName());
    }

    public Address readAddressByPostalCodeAndAddress(String postalCode, String address) {
        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }
        StringBuilder jpql = new StringBuilder("SELECT a FROM Address a WHERE LOWER(a.address) = LOWER(:address)");
        if (postalCode != null) {
            jpql.append(" AND a.postalCode = :postalCode");
        } else {
            jpql.append(" AND a.postalCode IS NULL");
        }
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Address> query = em.createQuery(jpql.toString(), Address.class)
                    .setParameter("address", address);
            if (postalCode != null) {
                query.setParameter("postalCode", postalCode);
            }
            return query.getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (PersistenceException e) {
            throw new DatabaseException("Reading address failed", e);
        }
    }
}
