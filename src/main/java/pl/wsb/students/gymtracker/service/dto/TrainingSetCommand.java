package pl.wsb.students.gymtracker.service.dto;

import java.math.BigDecimal;

public record TrainingSetCommand(
        Long exerciseId,
        Integer reps,
        BigDecimal weight
) {
}
