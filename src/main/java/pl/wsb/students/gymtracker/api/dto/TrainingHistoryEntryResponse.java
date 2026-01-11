package pl.wsb.students.gymtracker.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrainingHistoryEntryResponse(
        Long setId,
        LocalDate trainingDate,
        Integer reps,
        BigDecimal weight
) {
}
