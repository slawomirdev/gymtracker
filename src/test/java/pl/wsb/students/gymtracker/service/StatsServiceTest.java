package pl.wsb.students.gymtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.students.gymtracker.api.dto.StatsSummaryResponse;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.repository.ExerciseRepository;
import pl.wsb.students.gymtracker.repository.TrainingRepository;
import pl.wsb.students.gymtracker.repository.TrainingSetRepository;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingSetRepository trainingSetRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private StatsService statsService;

    @Test
    void summaryAggregatesValues() {
        Long userId = 1L;
        when(userService.getCurrentUserId()).thenReturn(userId);
        when(trainingRepository.countByUserId(userId)).thenReturn(4L);
        when(trainingSetRepository.countByTrainingUserId(userId)).thenReturn(18L);
        when(exerciseRepository.countByUserId(userId)).thenReturn(6L);
        when(trainingSetRepository.sumVolumeByUserId(userId)).thenReturn(new BigDecimal("2500"));
        Training latest = new Training();
        latest.setTrainingDate(LocalDate.of(2025, 1, 15));
        when(trainingRepository.findTopByUserIdOrderByTrainingDateDesc(userId)).thenReturn(Optional.of(latest));

        StatsSummaryResponse response = statsService.summary();

        assertThat(response.totalTrainings()).isEqualTo(4L);
        assertThat(response.totalSets()).isEqualTo(18L);
        assertThat(response.totalExercises()).isEqualTo(6L);
        assertThat(response.totalVolume()).isEqualTo(new BigDecimal("2500"));
        assertThat(response.lastTrainingDate()).isEqualTo(LocalDate.of(2025, 1, 15));
    }
}
