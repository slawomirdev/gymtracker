package pl.wsb.students.gymtracker.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TrainingCreateRequest(
        @NotNull(message = "Training date is required")
        LocalDate date,
        @Size(max = 500, message = "Note must be at most 500 characters")
        String note
) {
}
