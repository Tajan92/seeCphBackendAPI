package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.exceptions.DatabaseException;
import app.exceptions.DatabaseIdException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private AddressDAO addressDAO;
    private Map<String, Address> addressMap;

    @BeforeEach
    void setUp() {
        addressMap = TestDataCreator.createAddresses(emf);
    }

    @BeforeAll
    void setUpAll() {
        addressDAO = new AddressDAO(emf);
    }

    @Test
    void create() {
        Address address = Address.builder()
                .postalCode("1620")
                .city("København V")
                .address("Vesterbrogade 55")
                .build();

        Address addressCreated = addressDAO.create(address);
        assertThat(addressCreated.getId(), notNullValue());

        Address fetchedAddress = addressDAO.readById(addressCreated.getId());
        assertThat(fetchedAddress.getAddress(), equalTo(address.getAddress()));
        assertThat(fetchedAddress.getId(), is(addressCreated.getId()));
    }

    @Test
    void read() {
        Address address = addressMap.get("address");
        Address addressFetched = addressDAO.readById(address.getId());
        assertThat(addressFetched.getAddress(), equalTo(address.getAddress()));
        assertThat(addressFetched.getPostalCode(), equalTo(address.getPostalCode()));
        assertThat(addressFetched.getCity(), equalTo(address.getCity()));
        assertThat(addressFetched.getId(), is(address.getId()));
    }

    @Test
    void readAll() {
        List<Address> addressList = addressDAO.readAll();
        assertThat(addressList.size(), equalTo(addressMap.size()));
    }

    @Test
    void update() {
        Address address = addressMap.get("address");
        address.setPostalCode("1620");
        Address addressUpdated = addressDAO.update(address);

        assertThat(addressUpdated.getAddress(), equalTo(address.getAddress()));
        assertThat(addressUpdated.getId(), is(address.getId()));
    }

    @Test
    void delete() {
        Address address = addressMap.get("address");
        boolean deleted = addressDAO.delete(address);
        List<Address> addressList = addressDAO.readAll();

        assertThat(deleted, is(true));
        assertThat(addressList.size(), is(addressMap.size()-1));
    }

    @Test
    void create_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> addressDAO.create(null));
        assertThat(ex.getMessage(), is("Address is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> addressDAO.readById(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> addressDAO.readById(999_999));
        assertThat(ex.getMessage(), is("Address not found with id: 999999"));
    }

    @Test
    void update_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> addressDAO.update(null));
        assertThat(ex.getMessage(), is("Address is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        Address address = Address.builder()
                .id(999_999)
                .postalCode("2100")
                .city("København Ø")
                .address("Østerbrogade 120")
                .build();

        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> addressDAO.update(address));
        assertThat(ex.getMessage(), is("Address not found with id: "+address.getId()));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> addressDAO.delete(Address.builder().address("Håbet Allé 40").build()));
        assertThat(ex.getMessage(), is("Address id is required for deleting"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        Address address = Address.builder()
                .id(999_999)
                .postalCode("2100")
                .city("København Ø")
                .address("Østerbrogade 120")
                .build();

        DatabaseIdException ex = assertThrows(DatabaseIdException.class, () -> addressDAO.delete(address));
        assertThat(ex.getMessage(), is("Address not found with id: "+address.getId()));
    }
}