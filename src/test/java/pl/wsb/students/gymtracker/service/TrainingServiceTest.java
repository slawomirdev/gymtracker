package pl.wsb.students.gymtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.students.gymtracker.domain.AppUser;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.repository.TrainingRepository;
import pl.wsb.students.gymtracker.service.dto.TrainingCommand;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void createTrainingUsesUserAndData() {
        AppUser user = new AppUser();
        user.setId(5L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate date = LocalDate.of(2025, 1, 10);
        TrainingCommand request = new TrainingCommand(
                date,
                "Sila + mobilnosc",
                "srednia",
                "Silownia",
                null,
                75
        );
        Training saved = trainingService.createTraining(request);

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getTrainingDate()).isEqualTo(date);
        assertThat(saved.getNote()).isEqualTo("Sila + mobilnosc");
        verify(trainingRepository).save(any(Training.class));
    }
}
