package app.controllers;

import app.Populator;
import app.daos.impl.TripDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.TripCategory;
import app.exceptions.APIException;
import app.mapper.TripMapper;
import app.services.TripService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TripController {

    private static TripController instance;
    private final EntityManagerFactory emf;
    private final TripDAO tripDAO;

    private final ObjectMapper objectMapper;
    private final TripService tripService;

    private TripController(EntityManagerFactory emf) {
        this.tripDAO = TripDAO.getInstance(emf);
        this.emf = emf;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.tripService = TripService.getInstance(objectMapper);
    }

    public static TripController getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new TripController(emf);
        }

        return instance;
    }

    public void getAll(Context ctx) {
        Set<Trip> trips = tripDAO.getAll();
        Set<TripDTO> tripDTOSet = trips.stream().map(TripMapper::convertToDTO).collect(Collectors.toSet());

        ctx.status(HttpStatus.OK);
        ctx.json(tripDTOSet, TripDTO.class);
    }

    private final String[] packingListCategories = new String[]{
            "beach",
            "city",
            "forest",
            "lake",
            "sea",
            "snow"
    };

    public void getById(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            Trip trip = tripDAO.getById(id);
            TripDTO tripDTO = TripMapper.convertToDTO(trip);

            ObjectNode response = objectMapper.valueToTree(tripDTO);

            String packingListCategory = packingListCategories[Math.min(tripDTO.getCategory().ordinal(), packingListCategories.length - 1)];
            JsonNode packingListJson = tripService.getPackingItemsForTrip(packingListCategory);

            response.set("packingListItems", packingListJson.get("items"));

            ctx.status(HttpStatus.OK);
            ctx.json(response);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        } catch (EntityNotFoundException e) {
            throw new APIException(HttpStatus.NOT_FOUND, e.getMessage(), e.getCause());
        }
    }

    public void create(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyValidator(TripDTO.class).get();
            Trip trip = TripMapper.convertToEntity(tripDTO);

            Trip createdTrip = tripDAO.create(trip);
            TripDTO createdTripDTO = TripMapper.convertToDTO(createdTrip);

            ctx.status(HttpStatus.CREATED);
            ctx.json(createdTripDTO, TripDTO.class);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        }
    }

    public void update(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            TripDTO tripDTO = ctx.bodyValidator(TripDTO.class).get();
            Trip trip = TripMapper.convertToEntity(tripDTO);

            Trip updatedTrip = tripDAO.update(id, trip);
            TripDTO updatedTripDTO = TripMapper.convertToDTO(updatedTrip);

            ctx.status(HttpStatus.OK);
            ctx.json(updatedTripDTO, TripDTO.class);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        }
    }

    public void delete(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            tripDAO.delete(id);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        } catch (EntityNotFoundException e) {
            throw new APIException(HttpStatus.NOT_FOUND, e.getMessage(), e.getCause());
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            Long tripId = ctx.pathParamAsClass("tripId", Long.class).get();
            Long guideId = ctx.pathParamAsClass("guideId", Long.class).get();

            tripDAO.addGuideToTrip(tripId, guideId);

            ctx.status(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        } catch (EntityNotFoundException e) {
            throw new APIException(HttpStatus.NOT_FOUND, e.getMessage(), e.getCause());
        }
    }

    public void getTripsByCategory(Context ctx) {
        try {
            TripCategory tripCategory = ctx.pathParamAsClass("category", TripCategory.class).get();

            Set<Trip> trips = tripDAO.getAll();
            Set<Trip> filteredTrips = trips.stream().filter(trip -> trip.getCategory() == tripCategory).collect(Collectors.toSet());
            Set<TripDTO> tripDTOSet = filteredTrips.stream().map(TripMapper::convertToDTO).collect(Collectors.toSet());

            ctx.status(HttpStatus.OK);
            ctx.json(tripDTOSet, TripDTO.class);
        } catch (ValidationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, e.getErrors().toString(), e.getCause());
        }
    }

    public void getGuidesTotalPrice(Context ctx) {
        Set<Trip> trips = tripDAO.getAll();

        ArrayNode response = JsonNodeFactory.instance.arrayNode();

        Map<Guide, List<Trip>> guideTotalPriceMap = trips.stream()
                .filter(trip -> trip.getGuide() != null)
                .collect(Collectors.groupingBy(Trip::getGuide));

        guideTotalPriceMap.forEach((guide, guideTrips) -> {
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

            objectNode.put("guideId", guide.getId());
            objectNode.put("totalPrice", guideTrips.stream().mapToDouble(Trip::getPrice).sum());

            response.add(objectNode);
        });

        ctx.status(HttpStatus.OK);
        ctx.json(response);
    }

    public void populate(Context ctx) {
        Populator populator = Populator.getInstance(emf);
        populator.populateData();

        ctx.status(HttpStatus.NO_CONTENT);
    }
}
