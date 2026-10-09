package app.service;

import app.config.HibernateTestConfig;
import app.dao.AddressDAO;
import app.entities.Address;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressServiceTest {
    private EntityManagerFactory emf;
    private AddressDAO addressDAO;
    private AddressService addressService;

    @BeforeAll
    void setUpAll() {
        emf = HibernateTestConfig.getEntityManagerFactory();
        addressDAO = new AddressDAO(emf);
    }

    @BeforeEach
    void setUp() {
        addressService = new AddressService(addressDAO);
    }

    @Test
    public void createOrFindAddress_notExisting() {
        String postalCode = "2800";
        String city = "Lyngby";
        String street = "Hovedgaden 1";

        Address address = addressService.createOrFindAddress(postalCode, city, street);

        assertNotNull(address);
        assertThat(address.getId(), is(notNullValue()));
        assertThat(address.getPostalCode(), is(postalCode));
        assertThat(address.getCity(), is(city));
        assertThat(address.getAddress(), is(street));
    }

    @Test
    public void createOrFindAddress_Existing() {
        String postalCode = "2100";
        String city = "København Ø";
        String street = "Østerbrogade 100";

        Address firstAttempt = addressService.createOrFindAddress(postalCode, city, street);
        assertNotNull(firstAttempt);

        Address secondAttempt = addressService.createOrFindAddress(postalCode, city, street);
        assertNotNull(secondAttempt);
        assertThat(secondAttempt.getId(), is(firstAttempt.getId()));
        assertThat(secondAttempt.getPostalCode(), is(postalCode));
        assertThat(secondAttempt.getAddress(), is(street));
    }
}