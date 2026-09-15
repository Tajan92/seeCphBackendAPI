package app.dao;

import app.config.HibernateTestConfig;
import app.entities.Address;
import app.entities.Event;
import app.entities.ImageUrl;
import app.exceptions.DatabaseException;
import app.utils.TestDataCreator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageUrlDAOTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private ImageUrlDAO imageUrlDAO;
    private Map<String, ImageUrl> imageUrlMap;

    @BeforeEach
    void setUp() {
        imageUrlMap = TestDataCreator.createImageUrls(emf);
    }

    @BeforeAll
    void setUpAll() {
        imageUrlDAO = new ImageUrlDAO(emf);
    }
    @Test
    void create() {
        ImageUrl imageUrl = ImageUrl.builder()
                .url("randomUrl3.dk")
                .ratio("4_3")
                .height(400)
                .width(300)
                .build();

        ImageUrl imageUrlCreated = imageUrlDAO.create(imageUrl);
        assertThat(imageUrlCreated.getId(), notNullValue());

        ImageUrl imageUrlFetched = imageUrlDAO.read(imageUrlCreated.getId());
        assertThat(imageUrlFetched, is(imageUrlCreated));
        assertThat(imageUrlFetched.getUrl(), is(imageUrlCreated.getUrl()));
        assertThat(imageUrlFetched.getId(), is(imageUrlCreated.getId()));
    }

    @Test
    void read() {
        ImageUrl imageUrl = imageUrlMap.get("imageUrl");
        ImageUrl imageUrlFetched = imageUrlDAO.read(imageUrl.getId());

        assertThat(imageUrlFetched.getId(), is(imageUrl.getId()));
        assertThat(imageUrlFetched.getUrl(), is(imageUrl.getUrl()));
    }

    @Test
    void readAll() {
        Set<ImageUrl> imageUrlSet = imageUrlDAO.readAll();
        assertThat(imageUrlSet.size(), equalTo(imageUrlMap.size()));
    }

    @Test
    void update() {
        ImageUrl imageUrl = imageUrlMap.get("imageUrl");
        imageUrl.setHeight(700);
        ImageUrl imageUrlUpdated = imageUrlDAO.update(imageUrl);

        assertThat(imageUrlUpdated.getHeight(), equalTo(imageUrl.getHeight()));
        assertThat(imageUrlUpdated.getId(), is(imageUrl.getId()));
    }

    @Test
    void delete() {
        ImageUrl imageUrl = imageUrlMap.get("imageUrl");
        boolean deleted = imageUrlDAO.delete(imageUrl);

        Set<ImageUrl> imageUrlSet = imageUrlDAO.readAll();

        assertThat(deleted, is(true));
        assertThat(imageUrlSet.size(), is(imageUrlMap.size()-1));
    }

    @Test
    void create_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.create(null));
        assertThat(ex.getMessage(), is("ImageUrl is required"));
    }

    @Test
    void getById_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.read(null));
        assertThat(ex.getMessage(), is("ID is required"));
    }

    @Test
    void getById_withMissingId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.read(999_999));
        assertThat(ex.getMessage(), is("ImageUrl not found with id: 999999"));
    }

    @Test
    void update_withNullAdvert_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.update(null));
        assertThat(ex.getMessage(), is("ImageUrl is required for update"));
    }

    @Test
    void update_withMissingId_throwsApiException() {
        ImageUrl imageUrl = ImageUrl.builder()
                .id(999_999)
                .url("randomUrl3.dk")
                .ratio("4_3")
                .height(400)
                .width(300)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.update(imageUrl));
        assertThat(ex.getMessage(), is("Updating ImageUrl failed"));
    }

    @Test
    void delete_withNullId_throwsDatabaseException() {
        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.delete(null));
        assertThat(ex.getMessage(), is("ImageUrl is required for deletion"));
    }

    @Test
    void delete_withMissingId_throwsDatabaseException() {
        ImageUrl imageUrl = ImageUrl.builder()
                .id(999_999)
                .url("randomUrl3.dk")
                .ratio("4_3")
                .height(400)
                .width(300)
                .build();

        DatabaseException ex = assertThrows(DatabaseException.class, () -> imageUrlDAO.delete(imageUrl));
        assertThat(ex.getMessage(), is("Delete ImageUrl failed"));
    }
}