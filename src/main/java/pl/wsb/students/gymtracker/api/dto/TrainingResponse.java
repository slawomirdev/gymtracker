package pl.wsb.students.gymtracker.api.dto;

import java.time.LocalDate;
import java.util.List;

public record TrainingResponse(
        Long id,
        LocalDate date,
        String note,
        String intensity,
        String location,
        java.math.BigDecimal bodyWeight,
        Integer durationMinutes,
        List<TrainingSetResponse> sets
) {
}
