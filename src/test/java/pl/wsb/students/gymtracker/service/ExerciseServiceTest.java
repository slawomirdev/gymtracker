package pl.wsb.students.gymtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.students.gymtracker.domain.AppUser;
import pl.wsb.students.gymtracker.domain.Exercise;
import pl.wsb.students.gymtracker.repository.ExerciseRepository;
import pl.wsb.students.gymtracker.service.dto.ExerciseCommand;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void createExerciseAssignsUserAndPersists() {
        AppUser user = new AppUser();
        user.setId(10L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExerciseCommand request = new ExerciseCommand(
                "Wyciskanie",
                "Sila klatki",
                "https://example.com/wyciskanie.jpg",
                true
        );
        Exercise saved = exerciseService.createExercise(request);

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getName()).isEqualTo("Wyciskanie");
        assertThat(saved.getDescription()).isEqualTo("Sila klatki");
        assertThat(saved.getImageUrl()).isEqualTo("https://example.com/wyciskanie.jpg");
        verify(exerciseRepository).save(any(Exercise.class));
    }
}
