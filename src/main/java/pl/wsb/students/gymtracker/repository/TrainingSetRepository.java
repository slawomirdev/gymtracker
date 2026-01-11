package pl.wsb.students.gymtracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.wsb.students.gymtracker.domain.TrainingSet;

public interface TrainingSetRepository extends JpaRepository<TrainingSet, Long> {
    Optional<TrainingSet> findByIdAndTrainingUserId(Long id, Long userId);

    List<TrainingSet> findByExerciseIdAndTrainingUserIdOrderByTrainingTrainingDateDesc(Long exerciseId, Long userId);

    Page<TrainingSet> findByExerciseIdAndTrainingUserIdOrderByTrainingTrainingDateDesc(
            Long exerciseId, Long userId, Pageable pageable);

    long countByTrainingUserId(Long userId);

    @Query("""
            select coalesce(sum(s.weight * s.reps), 0)
            from TrainingSet s
            where s.training.user.id = :userId
            """)
    BigDecimal sumVolumeByUserId(@Param("userId") Long userId);

    @Query("""
            select coalesce(sum(s.reps), 0)
            from TrainingSet s
            where s.training.user.id = :userId
            """)
    Long sumRepsByUserId(@Param("userId") Long userId);

    @Query("""
            select e.id as exerciseId,
                   e.name as exerciseName,
                   count(s.id) as totalSets,
                   coalesce(max(s.weight), 0) as maxWeight,
                   coalesce(sum(s.weight * s.reps), 0) as totalVolume,
                   max(t.trainingDate) as lastTrainingDate
            from TrainingSet s
            join s.exercise e
            join s.training t
            where t.user.id = :userId
            group by e.id, e.name
            order by e.name
            """)
    List<ExerciseStatsProjection> findExerciseStats(@Param("userId") Long userId);

    interface ExerciseStatsProjection {
        Long getExerciseId();

        String getExerciseName();

        Long getTotalSets();

        BigDecimal getMaxWeight();

        BigDecimal getTotalVolume();

        LocalDate getLastTrainingDate();
    }
}
