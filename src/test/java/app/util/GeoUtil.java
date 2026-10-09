package app.util;

import app.entities.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeoUtil {
    private app.utils.GeoUtil.Coordinates coordinates;

    @BeforeAll
    void setUpAll() {
    }

    @BeforeEach
    void setUp() {
        coordinates = null;
    }

    @Test
    void newAddress() {
        Address address = Address.builder()
                .postalCode("1553")
                .city("København V")
                .address("H.C. Andersens Boulevard 44")
                .build();

        coordinates = app.utils.GeoUtil.findCoordinates(address);


        assertNotNull(coordinates);
        assertThat(coordinates.latitude(), is("55.6714790")); //Coordinates taken from https://nominatim.openstreetmap.org/search?q=H.C.+Andersens+Boulevard+44%2C+1553+K%C3%B8benhavn+V&format=json&limit=1
        assertThat(coordinates.longitude(), is("12.5757160"));
    }
}
