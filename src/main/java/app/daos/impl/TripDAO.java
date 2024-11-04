package app.daos.impl;

import app.daos.AbstractDAO;
import app.daos.ITripGuideDAO;
import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO extends AbstractDAO<Trip, Long> implements ITripGuideDAO {

    private static TripDAO instance;

    private TripDAO(EntityManagerFactory emf) {
        super(emf, Trip.class);
    }

    public static TripDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new TripDAO(emf);
        }

        return instance;
    }

    @Override
    public Trip update(Long id, Trip trip) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip foundTrip = em.find(Trip.class, id);

            if (foundTrip == null) {
                throw new EntityNotFoundException(String.format("Trip with id %d could not be found", id));
            }

            em.getTransaction().begin();

            if (trip.getStartTime() != null)
                foundTrip.setStartTime(trip.getStartTime());
            if (trip.getEndTime() != null)
                foundTrip.setEndTime(trip.getEndTime());
            if (trip.getStartPosition() != null)
                foundTrip.setStartPosition(trip.getStartPosition());
            if (trip.getName() != null)
                foundTrip.setName(trip.getName());
            if (trip.getPrice() != null)
                foundTrip.setPrice(trip.getPrice());
            if (trip.getCategory() != null)
                foundTrip.setCategory(trip.getCategory());

            if (trip.getGuide() != null) {
                Guide foundGuide = em.find(Guide.class, trip.getGuide().getId());

                if (foundGuide == null) {
                    throw new EntityNotFoundException(String.format("Guide with id %d could not be found", trip.getGuide().getId()));
                }

                foundTrip.setGuide(foundGuide);
            }

            em.getTransaction().commit();
            return foundTrip;
        }
    }

    @Override
    public void addGuideToTrip(Long tripId, Long guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip foundTrip = em.find(Trip.class, tripId);

            if (foundTrip == null) {
                throw new EntityNotFoundException(String.format("Trip with id %d could not be found", tripId));
            }

            Guide foundGuide = em.find(Guide.class, guideId);

            if (foundGuide == null) {
                throw new EntityNotFoundException(String.format("Guide with id %d could not be found", guideId));
            }

            em.getTransaction().begin();
            foundTrip.setGuide(foundGuide);
            em.getTransaction().commit();
        }
    }

    @Override
    public Set<Trip> getTripsByGuide(Long guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.guide.id = :guideId", Trip.class);
            query.setParameter("guideId", guideId);

            return query.getResultStream().collect(Collectors.toSet());
        }
    }
}
