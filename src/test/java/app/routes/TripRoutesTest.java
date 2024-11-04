package app.routes;

import app.Populator;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.controllers.SecurityController;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.dtos.UserDTO;
import app.entities.Guide;
import app.entities.Role;
import app.entities.Trip;
import app.entities.User;
import app.enums.TripCategory;
import app.mapper.GuideMapper;
import app.mapper.TripMapper;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TripRoutesTest {

    private static SecurityController securityController;
    private static Populator populator;
    private static Javalin app;

    private List<TripDTO> tripDTOList;
    private List<GuideDTO> guideDTOList;

    private String adminToken;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        securityController = SecurityController.getInstance(emf);
        populator = Populator.getInstance(emf);
        app = AppConfig.startServer(port, emf);

        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/v1";
        RestAssured.port = port;
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populator.createRoles();
        List<User> users = populator.createUsers(roles);

        populator.persist(roles);
        populator.persist(users);

        adminToken = securityController.createToken(new UserDTO("Admin1", "1234", Set.of("admin")));

        List<Trip> trips = populator.createTrips();
        List<Guide> guides = populator.createGuides(trips);

        populator.persist(guides);
        populator.persist(trips);

        tripDTOList = trips.stream().map(TripMapper::convertToDTO).toList();
        guideDTOList = guides.stream().map(GuideMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(User.class);
        populator.cleanup(Role.class);

        populator.cleanup(Trip.class);
        populator.cleanup(Guide.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void getAll() {
        List<TripDTO> expected = tripDTOList;

        List<TripDTO> actual = given()
                .when()
                .get("/trips")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("$", TripDTO.class);

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getById() {
        TripDTO expected = tripDTOList.get(0);

        given()
                .when()
                .get("/trips/{id}", expected.getId())
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("packingListItems", is(notNullValue()));
    }

    @Test
    void create() {
        TripDTO expected = new TripDTO(
                null,
                LocalTime.of(11, 30),
                LocalTime.of(14, 30),
                "Vineyard Entrance",
                "Wine Tasting Tour",
                89.99,
                TripCategory.CULINARY,
                null
        );

        TripDTO actual = given()
                .header("Authorization", String.format("Bearer %s", adminToken))
                .body(expected)
                .when()
                .post("/trips")
                .then()
                .statusCode(201)
                .extract()
                .as(TripDTO.class);

        expected.setId(actual.getId());

        assertThat(actual.getId(), is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    void update() {
        TripDTO expected = tripDTOList.get(0);

        expected.setName("Historical Landmarks Day Tour");

        given()
                .header("Authorization", String.format("Bearer %s", adminToken))
                .body(expected)
                .when()
                .put("/trips/{id}", expected.getId())
                .then()
                .statusCode(200)
                .body("id", is(expected.getId().intValue()))
                .body("name", is(expected.getName()));
    }

    @Test
    void delete() {
        TripDTO tripDTO = tripDTOList.get(0);

        given()
                .header("Authorization", String.format("Bearer %s", adminToken))
                .when()
                .delete("/trips/{id}", tripDTO.getId())
                .then()
                .statusCode(204);
    }
}