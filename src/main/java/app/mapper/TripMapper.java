package app.mapper;

import app.dtos.TripDTO;
import app.entities.Trip;

public class TripMapper {

    public static TripDTO convertToDTO(Trip trip) {
        return new TripDTO(
                trip.getId(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getStartPosition(),
                trip.getName(),
                trip.getPrice(),
                trip.getCategory(),
                trip.getGuide() != null ? GuideMapper.convertToDTO(trip.getGuide()) : null
        );
    }

    public static Trip convertToEntity(TripDTO tripDTO) {
        return Trip.builder()
                .id(tripDTO.getId())
                .startTime(tripDTO.getStartTime())
                .endTime(tripDTO.getEndTime())
                .startPosition(tripDTO.getStartPosition())
                .name(tripDTO.getName())
                .price(tripDTO.getPrice())
                .category(tripDTO.getCategory())
                .guide(tripDTO.getGuide() != null ? GuideMapper.convertToEntity(tripDTO.getGuide()) : null)
                .build();
    }
}
