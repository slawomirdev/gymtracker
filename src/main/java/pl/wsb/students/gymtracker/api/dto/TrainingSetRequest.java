package pl.wsb.students.gymtracker.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TrainingSetRequest(
        @NotNull(message = "Exercise is required")
        Long exerciseId,
        @NotNull(message = "Reps are required")
        @Positive(message = "Reps must be greater than 0")
        Integer reps,
        @NotNull(message = "Weight is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be 0 or more")
        BigDecimal weight
) {
}
