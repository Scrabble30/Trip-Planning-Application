package app.controllers;

import app.Populator;
import app.daos.impl.TripDAO;
import app.dtos.TripDTO;
import app.entities.Trip;
import app.exceptions.APIException;
import app.mapper.TripMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

public class TripController {

    private static TripController instance;
    private final EntityManagerFactory emf;
    private final TripDAO tripDAO;

    private TripController(EntityManagerFactory emf) {
        this.tripDAO = TripDAO.getInstance(emf);
        this.emf = emf;
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

    public void getById(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();

            Trip trip = tripDAO.getById(id);
            TripDTO tripDTO = TripMapper.convertToDTO(trip);

            ctx.status(HttpStatus.OK);
            ctx.json(tripDTO, TripDTO.class);
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

    public void populate(Context ctx) {
        Populator populator = Populator.getInstance(emf);
        populator.populateData();

        ctx.status(HttpStatus.NO_CONTENT);
    }
}
