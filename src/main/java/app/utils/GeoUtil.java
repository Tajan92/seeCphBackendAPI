package app.utils;

import app.entities.Address;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeoUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public record Coordinates(String latitude, String longitude) {
    }

    public static Coordinates findCoordinates(Address address) {
        if (address == null) {
            return new Coordinates("0", "0");
        }
        String query = String.format("%s, %s %s",
                address.getAddress(),
                address.getPostalCode() != null ? address.getPostalCode() : "",
                address.getCity()
        );
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1",encodedQuery);

        try {
            JsonNode rootNode = objectMapper.readTree(new URI(url).toURL());

            if (rootNode != null && !rootNode.isEmpty() && rootNode.isArray() ) {
                JsonNode firstResult = rootNode.get(0);

                String latitude = firstResult.has("lat") ? firstResult.get("lat").asText() : "0";
                String longitude = firstResult.has("lon") ? firstResult.get("lon").asText() : "0";

                return new Coordinates(latitude, longitude);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new Coordinates("0", "0");
    }



}

