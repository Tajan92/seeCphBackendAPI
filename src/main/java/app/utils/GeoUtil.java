package app.utils;

import app.entities.Address;
import app.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeoUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

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
        String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1", encodedQuery);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "seeCphBackendApp/1.0 (StudentProject)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());

                if (rootNode != null && !rootNode.isEmpty() && rootNode.isArray()) {
                    JsonNode firstResult = rootNode.get(0);

                    String latitude = firstResult.has("lat") ? firstResult.get("lat").asText() : "0";
                    String longitude = firstResult.has("lon") ? firstResult.get("lon").asText() : "0";

                    return new Coordinates(latitude, longitude);
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(500, "Failed to fetch coordinates from API: "+e.getMessage());
        }
        return new Coordinates("0", "0");
    }
}