package app.service;

import app.config.HibernateTestConfig;
import app.dto.ticketMaster.TicketMasterDTO;
import app.dto.ticketMaster.TmEvent;
import app.entities.Event;
import app.enums.EventCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketMasterConverterTest {

    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();
    private ObjectMapper objectMapper;
    private TicketMasterConverter ticketMasterConverter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ticketMasterConverter = new TicketMasterConverter();
    }

    @Test
    void ticketMasterDtoToEvent() {
        TicketMasterDTO dto;
        try {
            dto = objectMapper.readValue(getJsonEvent(), TicketMasterDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(dto);
        EventCategory eventCategory = null;
        Double price = null;
        for (TmEvent event : dto.embedded().events()) {
            eventCategory = event.classifications().getFirst().genre().name();
            price = event.priceRanges().getFirst().min();
        }
        Event convertedEvent = ticketMasterConverter.ticketMasterDtoToEvent(dto);
        assertNotNull(convertedEvent);
        assertThat(convertedEvent.getTitle(), is("Copenhagen Rock Festival 2026"));
        assertThat(convertedEvent.getCategory(),is(eventCategory));
        assertThat(convertedEvent.getCategory(), is(EventCategory.ROCK));
        assertThat(convertedEvent.getPrice(), is(price));
    }

    private String getJsonEvent() {
        return "{\n" +
                "  \"page\": {\n" +
                "    \"size\": 20,\n" +
                "    \"totalElements\": 1,\n" +
                "    \"totalPages\": 1,\n" +
                "    \"number\": 0\n" +
                "  },\n" +
                "  \"_embedded\": {\n" +
                "    \"events\": [\n" +
                "      {\n" +
                "        \"id\": \"Z7r9jZ1Ad7-_k\",\n" +
                "        \"name\": \"Copenhagen Rock Festival 2026\",\n" +
                "        \"url\": \"https://www.ticketmaster.dk/event/copenhagen-rock-2026\",\n" +
                "        \"info\": \"An amazing outdoor rock concert featuring top international and local artists.\",\n" +
                "        \"dates\": {\n" +
                "          \"start\": {\n" +
                "            \"localDate\": \"2026-07-15\",\n" +
                "            \"localTime\": \"19:30:00\",\n" +
                "            \"dateTime\": \"2026-07-15T19:30:00Z\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"classifications\": [\n" +
                "          {\n" +
                "            \"primary\": true,\n" +
                "            \"segment\": {\n" +
                "              \"name\": \"Music\"\n" +
                "            },\n" +
                "            \"genre\": {\n" +
                "              \"name\": \"Rock\"\n" +
                "            },\n" +
                "            \"subGenre\": {\n" +
                "              \"name\": \"Alternative\"\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"priceRanges\": [\n" +
                "          {\n" +
                "            \"min\": 450.0,\n" +
                "            \"max\": 850.0,\n" +
                "            \"currency\": \"DKK\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"_embedded\": {\n" +
                "          \"venues\": [\n" +
                "            {\n" +
                "              \"id\": \"V12345\",\n" +
                "              \"postalCode\": \"2100\",\n" +
                "              \"city\": {\n" +
                "                \"name\": \"Copenhagen\"\n" +
                "              },\n" +
                "              \"address\": {\n" +
                "                \"line1\": \"Øster Allé 50\"\n" +
                "              },\n" +
                "              \"location\": {\n" +
                "                \"longitude\": \"12.5683\",\n" +
                "                \"latitude\": \"55.7011\"\n" +
                "              }\n" +
                "            }\n" +
                "          ],\n" +
                "          \"attractions\": [\n" +
                "            {\n" +
                "              \"id\": \"A98765\",\n" +
                "              \"name\": \"The Cranberries Tribute\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"images\": [\n" +
                "          {\n" +
                "            \"ratio\": \"16_9\",\n" +
                "            \"url\": \"https://s1.ticketm.net/dam/a/123/image-ratio.jpg\",\n" +
                "            \"width\": 1024,\n" +
                "            \"height\": 576\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
    }
}