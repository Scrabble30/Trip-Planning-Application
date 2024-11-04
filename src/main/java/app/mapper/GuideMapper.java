package app.mapper;

import app.dtos.GuideDTO;
import app.entities.Guide;

public class GuideMapper {

    public static GuideDTO convertToDTO(Guide guide) {
        return new GuideDTO(
                guide.getId(),
                guide.getFirstName(),
                guide.getLastName(),
                guide.getEmail(),
                guide.getPhone(),
                guide.getYearsOfExperience()
        );
    }

    public static Guide convertToEntity(GuideDTO guideDTO) {
        return Guide.builder()
                .id(guideDTO.getId())
                .firstName(guideDTO.getFirstName())
                .lastName(guideDTO.getLastName())
                .email(guideDTO.getEmail())
                .phone(guideDTO.getPhone())
                .yearsOfExperience(guideDTO.getYearsOfExperience())
                .build();
    }
}
