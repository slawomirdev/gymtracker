package pl.wsb.students.gymtracker.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.students.gymtracker.api.dto.TrainingSetRequest;
import pl.wsb.students.gymtracker.api.error.NotFoundException;
import pl.wsb.students.gymtracker.domain.Exercise;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.domain.TrainingSet;
import pl.wsb.students.gymtracker.repository.TrainingSetRepository;

@Service
public class TrainingSetService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSetService.class);

    private final TrainingSetRepository trainingSetRepository;
    private final TrainingService trainingService;
    private final ExerciseService exerciseService;

    public TrainingSetService(TrainingSetRepository trainingSetRepository,
                              TrainingService trainingService,
                              ExerciseService exerciseService) {
        this.trainingSetRepository = trainingSetRepository;
        this.trainingService = trainingService;
        this.exerciseService = exerciseService;
    }

    @Transactional
    public TrainingSet addSet(Long trainingId, TrainingSetRequest request) {
        Training training = trainingService.getTraining(trainingId);
        Exercise exercise = exerciseService.getExercise(request.exerciseId());
        TrainingSet set = new TrainingSet();
        set.setTraining(training);
        set.setExercise(exercise);
        set.setReps(request.reps());
        set.setWeight(request.weight());
        TrainingSet saved = trainingSetRepository.save(set);
        logger.info("Added set {} to training {}", saved.getId(), trainingId);
        return saved;
    }

    @Transactional
    public void deleteSet(Long trainingId, Long setId, Long userId) {
        TrainingSet set = trainingSetRepository.findByIdAndTrainingUserId(setId, userId)
                .orElseThrow(() -> new NotFoundException("Set not found"));
        if (!set.getTraining().getId().equals(trainingId)) {
            throw new NotFoundException("Set not found");
        }
        trainingSetRepository.delete(set);
        logger.info("Deleted set {}", setId);
    }

    @Transactional(readOnly = true)
    public List<TrainingSet> history(Long exerciseId, Long userId) {
        return trainingSetRepository.findByExerciseIdAndTrainingUserIdOrderByTrainingTrainingDateDesc(
                exerciseId, userId);
    }
}
