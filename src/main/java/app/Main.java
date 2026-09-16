package app;

import app.config.HibernateConfig;
import app.utils.APIReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        APIReader apiReader = new APIReader();
        ObjectMapper objectMapper = new ObjectMapper();

        String apiKeyTicketMaster = System.getenv("API_KEY");
        System.out.println("apiKeyTicketMaster: " + apiKeyTicketMaster);
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&sort=date,asc&size=1&apikey=" + apiKeyTicketMaster;
        String url2 = "https://app.ticketmaster.com/discovery/v2/classifications?apikey=" + apiKeyTicketMaster;
        HttpRequest request = null;
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url2))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            emf.close();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

}

