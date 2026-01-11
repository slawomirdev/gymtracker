package pl.wsb.students.gymtracker.api.dto;

import java.math.BigDecimal;

public record TrainingSetResponse(
        Long id,
        Long exerciseId,
        String exerciseName,
        Integer reps,
        BigDecimal weight
) {
}
