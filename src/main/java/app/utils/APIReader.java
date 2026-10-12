package app.utils;

import app.dto.ticketMaster.TicketMasterDTO;
import app.entities.Event;
import app.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class APIReader {
    private static final String GEMINI_MODEL = "gemini-3.5-flash-lite";
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";
    private static final String TM_URL = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&sort=date,asc&size=%d&page=%d&apikey=%s";
    private static final int TM_PAGE_SIZE = 200;
    private static final int TM_MAX_PAGES = 1000 / TM_PAGE_SIZE;
    private final String geminiApiKey = System.getenv("GEMINI_API_KEY");
    private final String ticketMasterApiKey = System.getenv("TICKETMASTER_API_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ===== TICKETMASTER =====
    public List<TicketMasterDTO> getApiAsTmDTO() {
        if (ticketMasterApiKey == null || ticketMasterApiKey.isBlank()) {
            throw new ApiException(500, "TICKETMASTER_API_KEY is not configured");
        }

        List<TicketMasterDTO> ticketMasterDTOs = new ArrayList<>();
        try {
            JsonNode firstPage = fetchTmPage(0);
            ticketMasterDTOs.add(objectMapper.treeToValue(firstPage, TicketMasterDTO.class));

            int totalPages = Math.min(firstPage.path("page").path("totalPages").asInt(1), TM_MAX_PAGES);
            for (int i = 1; i < totalPages; i++) {
                Thread.sleep(250);
                ticketMasterDTOs.add(objectMapper.treeToValue(fetchTmPage(i), TicketMasterDTO.class));
            }
        } catch (IOException e) {
            throw new ApiException(502, "Failed to fetch Ticket Master API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(500, "Ticketmaster thread was interrupted", e);
        }
        return ticketMasterDTOs;
    }

    private JsonNode fetchTmPage(int page) throws IOException, InterruptedException {
        String url = String.format(TM_URL, TM_PAGE_SIZE, page, ticketMasterApiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Ticketmaster returned status " + response.statusCode() + " for page " + page);
        }
        return objectMapper.readTree(response.body());
    }

    // ===== GEMINI =====

    public String geminiDescriptionCreator(Event event) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return DefaultDescription.generateDefaultDescription(event);
        }
        try {
            String address = event.getLocation() != null ? event.getLocation().getAddress() : "";
            String prompt = "Write a very short, single-sentence description in English for the event: "
                    + event.getTitle() + " at " + address + " (Link: " + event.getUrl() + ")";

            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                    "generationConfig", Map.of("maxOutputTokens", 60));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_ENDPOINT))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", geminiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("Gemini returned status {} for event '{}'", response.statusCode(), event.getTitle());
                return DefaultDescription.generateDefaultDescription(event);
            }

            String aiText = objectMapper.readTree(response.body())
                    .path("candidates").path(0).path("content").path("parts").path(0).path("text")
                    .asText();

            if (aiText.isBlank()) {
                return DefaultDescription.generateDefaultDescription(event);
            }
            return aiText.trim() + " (Description created with AI)";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return DefaultDescription.generateDefaultDescription(event);
        } catch (Exception e) {
            log.warn("Gemini description failed for event '{}': {}", event.getTitle(), e.getMessage());
            return DefaultDescription.generateDefaultDescription(event);
        }
    }
}