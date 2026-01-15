package pl.wsb.students.gymtracker.service.dto;

public record ExerciseCommand(
        String name,
        String description,
        String imageUrl,
        Boolean active
) {
}
