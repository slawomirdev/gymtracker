package pl.wsb.students.gymtracker.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.students.gymtracker.api.dto.ExerciseStatsResponse;
import pl.wsb.students.gymtracker.api.dto.StatsSummaryResponse;
import pl.wsb.students.gymtracker.repository.ExerciseRepository;
import pl.wsb.students.gymtracker.repository.TrainingRepository;
import pl.wsb.students.gymtracker.repository.TrainingSetRepository;

@Service
public class StatsService {

    private final TrainingRepository trainingRepository;
    private final TrainingSetRepository trainingSetRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserService userService;

    public StatsService(TrainingRepository trainingRepository,
                        TrainingSetRepository trainingSetRepository,
                        ExerciseRepository exerciseRepository,
                        UserService userService) {
        this.trainingRepository = trainingRepository;
        this.trainingSetRepository = trainingSetRepository;
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public StatsSummaryResponse summary() {
        Long userId = userService.getCurrentUserId();
        long totalTrainings = trainingRepository.countByUserId(userId);
        long totalSets = trainingSetRepository.countByTrainingUserId(userId);
        long totalExercises = exerciseRepository.countByUserId(userId);
        BigDecimal totalVolume = trainingSetRepository.sumVolumeByUserId(userId);
        var lastTrainingDate = trainingRepository.findTopByUserIdOrderByTrainingDateDesc(userId)
                .map(training -> training.getTrainingDate())
                .orElse(null);
        return new StatsSummaryResponse(totalTrainings, totalSets, totalExercises, totalVolume, lastTrainingDate);
    }

    @Transactional(readOnly = true)
    public List<ExerciseStatsResponse> exerciseStats() {
        Long userId = userService.getCurrentUserId();
        return trainingSetRepository.findExerciseStats(userId).stream()
                .map(row -> new ExerciseStatsResponse(
                        row.getExerciseId(),
                        row.getExerciseName(),
                        row.getTotalSets(),
                        row.getMaxWeight(),
                        row.getTotalVolume(),
                        row.getLastTrainingDate()))
                .toList();
    }
}
