package app.routes;

import app.controllers.TripController;
import app.enums.AppRouteRole;
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
            get("/", tripController::getAll, AppRouteRole.ANYONE);
            get("/{id}", tripController::getById, AppRouteRole.ANYONE);
            post("/", tripController::create, AppRouteRole.ADMIN);
            put("/{id}", tripController::update, AppRouteRole.ADMIN);
            delete("/{id}", tripController::delete, AppRouteRole.ADMIN);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, AppRouteRole.ADMIN);
            get("/categories/{category}", tripController::getTripsByCategory, AppRouteRole.ANYONE);
            get("/guides/totalprice", tripController::getGuidesTotalPrice, AppRouteRole.ANYONE);
            get("/{tripId}/packingitems/weightsum", tripController::getWeightSumOfTripPackingItems, AppRouteRole.ANYONE);
            post("/populate", tripController::populate, AppRouteRole.ADMIN);
        };
    }
}
