package app;

import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Role;
import app.entities.Trip;
import app.entities.User;
import app.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class Populator {

    private static Populator instance;
    private final EntityManagerFactory emf;

    private Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static Populator getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new Populator(emf);
        }

        return instance;
    }

    public void populateData() {
        // Create roles and users
        List<Role> roles = createRoles();
        persist(roles);

        List<User> users = createUsers(roles);
        persist(users);

        // Create trips and guides
        List<Trip> trips = createTrips();
        List<Guide> guides = createGuides(trips);

        persist(guides);
        persist(trips);
    }

    public List<Trip> createTrips() {
        return List.of(
                Trip.builder()
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(12, 0))
                        .startPosition("Central Station")
                        .name("Morning City Tour")
                        .price(49.99)
                        .category(TripCategory.SIGHTSEEING)
                        .build(),
                Trip.builder()
                        .startTime(LocalTime.of(14, 30))
                        .endTime(LocalTime.of(18, 0))
                        .startPosition("Harbor")
                        .name("Afternoon Boat Cruise")
                        .price(79.99)
                        .category(TripCategory.CRUISE)
                        .build(),
                Trip.builder()
                        .startTime(LocalTime.of(20, 0))
                        .endTime(LocalTime.of(23, 0))
                        .startPosition("Old Town Square")
                        .name("Evening Food Tour")
                        .price(59.99)
                        .category(TripCategory.CULINARY)
                        .build(),
                Trip.builder()
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(16, 0))
                        .startPosition("National Park Entrance")
                        .name("Hiking Adventure")
                        .price(39.99)
                        .category(TripCategory.ADVENTURE)
                        .build()
        );
    }

    public List<Guide> createGuides(List<Trip> trips) {
        List<Guide> guides = List.of(
                Guide.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .email("john.smith@example.com")
                        .phone("+1-555-123-4567")
                        .yearsOfExperience(5)
                        .build(),
                Guide.builder()
                        .firstName("Emma")
                        .lastName("Johnson")
                        .email("emma.johnson@example.com")
                        .phone("+1-555-987-6543")
                        .yearsOfExperience(8)
                        .build()
        );

        trips.get(0).setGuide(guides.get(0));
        trips.get(1).setGuide(guides.get(0));
        trips.get(2).setGuide(guides.get(1));

        return guides;
    }

    public List<Role> createRoles() {
        return List.of(
                new Role("user"),
                new Role("admin")
        );
    }

    public List<User> createUsers(List<Role> roles) {
        return List.of(
                new User(
                        "User1",
                        "1234",
                        Set.of(roles.get(0))
                ),
                new User(
                        "User2",
                        "1234",
                        Set.of(roles.get(0))
                ),
                new User(
                        "Admin1",
                        "1234",
                        Set.of(roles.get(1))
                )
        );
    }

    public void persist(List<?> entities) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            entities.forEach(em::persist);
            em.getTransaction().commit();
        }
    }

    public void cleanup(Class<?> entityClass) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
            em.getTransaction().commit();
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("trip_planning_db");

        Populator populator = Populator.getInstance(emf);
        populator.populateData();
    }
}
