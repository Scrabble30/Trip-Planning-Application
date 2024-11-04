package app.daos;

import app.entities.Trip;

import java.util.Set;

public interface ITripGuideDAO {

    void addGuideToTrip(Long tripId, Long guideId);

    Set<Trip> getTripsByGuide(Long guideId);
}
