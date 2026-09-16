package app.utils;

import app.dto.ticketMaster.TicketMasterDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class APIReader {
    private final ObjectMapper objectMapper = new ObjectMapper();
    String apiKey = System.getenv("GEMINI_API_KEY");

    public <T> T getApiAsDTO(String url, Class<T> tclass) {
        try {
            JsonNode node = objectMapper.readTree(new URI(url).toURL());
            return objectMapper.treeToValue(node, tclass);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TicketMasterDTO> getApiAsTmDTO(String url) {
        List<TicketMasterDTO> ticketMasterDTOs = new ArrayList<>();
        String firstUrl = url.replace("$", "1");
        try {
            JsonNode node = objectMapper.readTree(new URI(firstUrl).toURL());
            TicketMasterDTO firstDto = objectMapper.treeToValue(node, TicketMasterDTO.class);
            ticketMasterDTOs.add(firstDto);
            JsonNode page = node.get("page");
            int totalPages = page.get("totalPages").asInt();
            List<String> urls = new ArrayList<>();
            for (int i = 2; i <= totalPages; i++) {
                urls.add(url.replace("$", String.valueOf(i)));
            }
            for (String pageUrl : urls) {
                JsonNode pageNode = objectMapper.readTree(new URI(pageUrl).toURL());
                TicketMasterDTO pageDto = objectMapper.treeToValue(pageNode, TicketMasterDTO.class);
                ticketMasterDTOs.add(pageDto);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ticketMasterDTOs;
    }

    public String geminiRequest(String prompt) {
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
                .header("x-goog-api-key", apiKey)
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
