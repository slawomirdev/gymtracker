package pl.wsb.students.gymtracker.api.dto;

public record ExerciseResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        Boolean active
) {
}
