package app.dtos;

import app.enums.TripCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalTime endTime;
    private String startPosition;
    private String name;
    private Double price;
    private TripCategory category;
    private GuideDTO guide;
}
