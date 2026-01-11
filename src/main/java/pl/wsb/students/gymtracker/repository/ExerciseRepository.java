package pl.wsb.students.gymtracker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.students.gymtracker.domain.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findAllByUserIdOrderByNameAsc(Long userId);

    long countByUserId(Long userId);
}
