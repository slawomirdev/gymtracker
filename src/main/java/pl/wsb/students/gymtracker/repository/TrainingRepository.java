package pl.wsb.students.gymtracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.students.gymtracker.domain.Training;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    @EntityGraph(attributePaths = {"sets", "sets.exercise"})
    List<Training> findAllByUserIdOrderByTrainingDateDesc(Long userId);

    @EntityGraph(attributePaths = {"sets", "sets.exercise"})
    Optional<Training> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    Optional<Training> findTopByUserIdOrderByTrainingDateDesc(Long userId);
}
