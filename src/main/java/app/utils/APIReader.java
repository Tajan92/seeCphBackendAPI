package app.utils;

import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.ticketMaster.TmEvent;
import app.entities.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class APIReader {
    private final ObjectMapper objectMapper = new ObjectMapper();
    String geminiApiKey = System.getenv("GEMINI_API_KEY");

    public <T> T getApiAsDTO(String url, Class<T> tclass) {
        try {
            JsonNode node = objectMapper.readTree(new URI(url).toURL());
            return objectMapper.treeToValue(node, tclass);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TicketMasterDTO> getApiAsTmDTO() {
        String apiKeyTicketMaster = System.getenv("TICKETMASTER_API_KEY");
        String urlTemplate = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&sort=date,asc&page=$&apikey=" + apiKeyTicketMaster;

        List<TicketMasterDTO> ticketMasterDTOs = new ArrayList<>();

        try {
            String firstUrl = urlTemplate.replace("$", "0");
            JsonNode node = objectMapper.readTree(new URI(firstUrl).toURL());
            TicketMasterDTO firstDto = objectMapper.treeToValue(node, TicketMasterDTO.class);
            ticketMasterDTOs.add(firstDto);

            JsonNode page = node.get("page");
            int totalPages = page.get("totalPages").asInt();

            for (int i = 1; i < totalPages; i++) {
                String pageUrl = urlTemplate.replace("$", String.valueOf(i));

                JsonNode pageNode = objectMapper.readTree(new URI(pageUrl).toURL());
                TicketMasterDTO pageDto = objectMapper.treeToValue(pageNode, TicketMasterDTO.class);
                ticketMasterDTOs.add(pageDto);

                Thread.sleep(250);
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return ticketMasterDTOs;
    }

    public String geminiDescriptionCreator(Event event) {
        String prompt = "Write a very short, single-sentence description in English for the event: "
                + event.getTitle() + " at " + event.getLocation().getAddress() + " (Link: " + event.getUrl() + ")";

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of("maxOutputTokens", 60)
        );
        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String model = "gemini-3.5-flash-lite";
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", geminiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());
            String aiText = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            return aiText + " (Description created with AI)";
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return DefaultDescription.generateDefaultDescription(event);
        }
    }


    public String geminiRequestGenericPrompt(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String model = "gemini-3.5-flash-lite";
        String endpoint =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", geminiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }
}
