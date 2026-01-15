package pl.wsb.students.gymtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.students.gymtracker.domain.Exercise;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.domain.TrainingSet;
import pl.wsb.students.gymtracker.repository.TrainingSetRepository;
import pl.wsb.students.gymtracker.service.dto.TrainingSetCommand;

@ExtendWith(MockitoExtension.class)
class TrainingSetServiceTest {

    @Mock
    private TrainingSetRepository trainingSetRepository;

    @Mock
    private TrainingService trainingService;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private TrainingSetService trainingSetService;

    @Test
    void addSetAssignsTrainingAndExercise() {
        Training training = new Training();
        training.setId(3L);
        Exercise exercise = new Exercise();
        exercise.setId(9L);
        when(trainingService.getTraining(3L)).thenReturn(training);
        when(exerciseService.getExercise(9L)).thenReturn(exercise);
        when(trainingSetRepository.save(any(TrainingSet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainingSetCommand request = new TrainingSetCommand(9L, 8, new BigDecimal("100"));
        TrainingSet saved = trainingSetService.addSet(3L, request);

        assertThat(saved.getTraining()).isEqualTo(training);
        assertThat(saved.getExercise()).isEqualTo(exercise);
        assertThat(saved.getReps()).isEqualTo(8);
        assertThat(saved.getWeight()).isEqualTo(new BigDecimal("100"));
        verify(trainingSetRepository).save(any(TrainingSet.class));
    }
}
