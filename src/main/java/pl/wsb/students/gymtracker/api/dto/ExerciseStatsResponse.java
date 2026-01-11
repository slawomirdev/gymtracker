package pl.wsb.students.gymtracker.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExerciseStatsResponse(
        Long exerciseId,
        String exerciseName,
        Long totalSets,
        BigDecimal maxWeight,
        BigDecimal totalVolume,
        LocalDate lastTrainingDate
) {
}
