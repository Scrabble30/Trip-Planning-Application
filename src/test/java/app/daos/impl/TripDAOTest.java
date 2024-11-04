package app.daos.impl;

import app.Populator;
import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.TripCategory;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class TripDAOTest {

    private static Populator populator;
    private static TripDAO tripDAO;

    private List<Trip> trips;
    private List<Guide> guides;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = Populator.getInstance(emf);
        tripDAO = TripDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        trips = populator.createTrips();
        guides = populator.createGuides(trips);

        populator.persist(guides);
        populator.persist(trips);
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(Trip.class);
        populator.cleanup(Guide.class);
    }

    @Test
    void create() {
        Trip expected = Trip.builder()
                .startTime(LocalTime.of(11, 30))
                .endTime(LocalTime.of(14, 30))
                .startPosition("Vineyard Entrance")
                .name("Wine Tasting Tour")
                .price(89.99)
                .category(TripCategory.CULINARY)
                .build();

        Trip actual = tripDAO.create(expected);

        expected.setId(actual.getId());

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    void getById() {
        Trip expected = trips.get(0);
        Trip actual = tripDAO.getById(expected.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void getAll() {
        Set<Trip> expected = new HashSet<>(trips);
        Set<Trip> actual = tripDAO.getAll();

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void update() {
        Trip expected = trips.get(0);

        expected.setStartTime(LocalTime.of(8, 0));
        expected.setName("Historical Landmarks Day Tour");
        expected.setPrice(129.99);

        Trip actual = tripDAO.update(expected.getId(), expected);

        assertThat(actual, is(expected));
    }

    @Test
    void delete() {
        Trip trip = trips.get(0);

        tripDAO.delete(trip.getId());
        assertThrowsExactly(EntityNotFoundException.class, () -> tripDAO.getById(trip.getId()));
    }

    @Test
    void addGuideToTrip() {
        Trip trip = trips.get(trips.size() - 1);
        Guide guide = guides.get(0);

        tripDAO.addGuideToTrip(trip.getId(), guide.getId());

        Trip actual = tripDAO.getById(trip.getId());

        assertThat(actual.getGuide(), is(guide));
    }

    @Test
    void getTripsByGuide() {
        Guide guide = guides.get(0);

        Set<Trip> expected = trips.stream().filter(trip -> Objects.equals(trip.getGuide(), guide)).collect(Collectors.toSet());
        Set<Trip> actual = tripDAO.getTripsByGuide(guide.getId());

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}