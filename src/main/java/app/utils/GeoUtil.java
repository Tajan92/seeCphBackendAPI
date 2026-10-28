package app.utils;

import app.entities.Address;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class GeoUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public record Coordinates(String latitude, String longitude) {
    }

    public static Coordinates findCoordinates(Address address) {
        if (address == null) {
            return null;
        }
        String query = String.format("%s, %s %s",
                address.getAddress(),
                address.getPostalCode() != null ? address.getPostalCode() : "",
                address.getCity()  != null ? address.getCity() : ""
        );
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1", encodedQuery);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "seeCphBackendApp/1.0 (StudentProject)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.getCode()) {
                JsonNode rootNode = objectMapper.readTree(response.body());

                if (rootNode != null && !rootNode.isEmpty() && rootNode.isArray()) {
                    JsonNode firstResult = rootNode.get(0);

                    String latitude = firstResult.has("lat") ? firstResult.get("lat").asText() : null;
                    String longitude = firstResult.has("lon") ? firstResult.get("lon").asText() : null;

                    return new Coordinates(latitude, longitude);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Getting coordinates from API was interrupted {}", query, e);
        } catch (IOException e) {
            log.warn("Failed to fetch coordinates from API {}", query, e);
        }
        return null;
    }
}