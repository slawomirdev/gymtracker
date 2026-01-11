package pl.wsb.students.gymtracker.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsSummaryResponse(
        long totalTrainings,
        long totalSets,
        long totalReps,
        long totalExercises,
        BigDecimal totalVolume,
        LocalDate lastTrainingDate
) {
}
