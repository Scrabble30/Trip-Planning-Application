package app.routes;

import app.controllers.TripController;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController;

    public TripRoutes(EntityManagerFactory emf) {
        this.tripController = TripController.getInstance(emf);
    }

    public EndpointGroup getTripRoutes() {
        return () -> {
            get("/", tripController::getAll);
            get("/{id}", tripController::getById);
            post("/", tripController::create);
            put("/{id}", tripController::update);
            delete("/{id}", tripController::delete);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip);
            get("/categories/{category}", tripController::getTripsByCategory);
            get("/guides/totalprice", tripController::getGuidesTotalPrice);
            post("/populate", tripController::populate);
        };
    }
}
