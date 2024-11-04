package app.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TripService {

    private final Logger logger = LoggerFactory.getLogger(TripService.class);
    private static TripService instance;

    private final String BASE_URL = "https://packingapi.cphbusinessapps.dk";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    private TripService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static TripService getInstance(ObjectMapper objectMapper) {
        if (instance == null) {
            instance = new TripService(objectMapper);
        }

        return instance;
    }

    public JsonNode getPackingItemsForTrip(String category) {
        String url = String.format("%s/packinglist/%s", BASE_URL, category);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "Application/json")
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readTree(response.body());
            } else {
                logger.error("Failed to retrieve packing items for trip: {} - Response: {}", category, response);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to retrieve packing items for trip: {}", category);
            return null;
        }
    }
}
