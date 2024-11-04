package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final TripRoutes tripRoutes;

    public Routes(EntityManagerFactory emf) {
        this.tripRoutes = new TripRoutes(emf);
    }

    public EndpointGroup getAPIRoutes() {
        return () -> {
            path("/trips", tripRoutes.getTripRoutes());
        };
    }
}
