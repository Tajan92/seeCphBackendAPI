package app;

import app.config.HibernateConfig;
import app.controller.EventHandler;
import app.dao.*;
import app.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import jakarta.persistence.EntityManagerFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
            }));
        });
        app.start(7070);

        // DAOs
        AddressDAO addressDAO = new AddressDAO(emf);
        AdvertDAO advertDAO = new AdvertDAO(emf);
        EventDAO eventDAO = new EventDAO(emf);
        ImageUrlDAO imageUrlDAO = new ImageUrlDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        // Services
        EventService eventService = new EventService(eventDAO);


        // Handlers
        EventHandler eventHandler = new EventHandler(eventDAO);

        // Routes



    }
}
//        eventService.persistEvents();
//        APIReader apiReader = new APIReader();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String apiKeyTicketMaster = System.getenv("API_KEY");
//        System.out.println("apiKeyTicketMaster: " + apiKeyTicketMaster);
//        String url = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=DK&latlong=55.6761,12.5683&radius=15&unit=km&sort=date,asc&size=1&apikey=" + apiKeyTicketMaster;
//        String url2 = "https://app.ticketmaster.com/discovery/v2/classifications?apikey=" + apiKeyTicketMaster;
//        HttpRequest request = null;
//        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
//            request = HttpRequest.newBuilder()
//                    .uri(new URI(url2))
//                    .timeout(Duration.ofSeconds(10))
//                    .header("Accept", "application/json")
//                    .GET()
//                    .build();
//            try {
//                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//                System.out.println(response.body());
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            emf.close();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//
