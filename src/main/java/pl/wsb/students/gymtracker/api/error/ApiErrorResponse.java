package pl.wsb.students.gymtracker.api.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        String code,
        String message,
        Instant timestamp,
        List<FieldError> details
) {
    public record FieldError(String field, String message) {
    }
}
