package pl.wsb.students.gymtracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.students.gymtracker.domain.TrainingSet;

public interface TrainingSetRepository extends JpaRepository<TrainingSet, Long> {
    Optional<TrainingSet> findByIdAndTrainingUserId(Long id, Long userId);

    List<TrainingSet> findByExerciseIdAndTrainingUserIdOrderByTrainingTrainingDateDesc(Long exerciseId, Long userId);
}
