package pl.wsb.students.gymtracker.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrainingCommand(
        LocalDate date,
        String note,
        String intensity,
        String location,
        BigDecimal bodyWeight,
        Integer durationMinutes
) {
}
