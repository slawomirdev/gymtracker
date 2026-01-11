package pl.wsb.students.gymtracker.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TrainingCreateRequest(
        @NotNull(message = "Training date is required")
        LocalDate date,
        @Size(max = 500, message = "Note must be at most 500 characters")
        String note,
        @Pattern(regexp = "^(niska|srednia|wysoka)$", message = "Intensity must be: niska, srednia, wysoka")
        String intensity,
        @Size(max = 120, message = "Location must be at most 120 characters")
        String location,
        @DecimalMin(value = "0.0", inclusive = true, message = "Body weight must be 0 or more")
        BigDecimal bodyWeight,
        @Positive(message = "Duration must be greater than 0")
        Integer durationMinutes
) {
}
