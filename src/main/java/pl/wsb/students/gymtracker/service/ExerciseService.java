package pl.wsb.students.gymtracker.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.students.gymtracker.api.error.NotFoundException;
import pl.wsb.students.gymtracker.domain.AppUser;
import pl.wsb.students.gymtracker.domain.Exercise;
import pl.wsb.students.gymtracker.repository.ExerciseRepository;
import pl.wsb.students.gymtracker.service.dto.ExerciseCommand;

@Service
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    private final ExerciseRepository exerciseRepository;
    private final UserService userService;

    public ExerciseService(ExerciseRepository exerciseRepository, UserService userService) {
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Exercise> listExercises() {
        AppUser user = userService.getCurrentUser();
        return exerciseRepository.findAllByUserIdOrderByNameAsc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Exercise> listActiveExercises() {
        AppUser user = userService.getCurrentUser();
        return exerciseRepository.findAllByUserIdAndActiveTrueOrderByNameAsc(user.getId());
    }

    @Transactional(readOnly = true)
    public Exercise getExercise(Long id) {
        AppUser user = userService.getCurrentUser();
        return exerciseRepository.findById(id)
                .filter(exercise -> exercise.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
    }

    @Transactional
    public Exercise createExercise(ExerciseCommand command) {
        AppUser user = userService.getCurrentUser();
        Exercise exercise = new Exercise();
        exercise.setUser(user);
        exercise.setName(command.name());
        exercise.setDescription(command.description());
        exercise.setImageUrl(command.imageUrl());
        exercise.setActive(command.active() == null || command.active());
        Exercise saved = exerciseRepository.save(exercise);
        logger.info("Created exercise {} for user {}", saved.getId(), user.getId());
        return saved;
    }

    @Transactional
    public Exercise updateExercise(Long id, ExerciseCommand command) {
        Exercise exercise = getExercise(id);
        exercise.setName(command.name());
        exercise.setDescription(command.description());
        exercise.setImageUrl(command.imageUrl());
        if (command.active() != null) {
            exercise.setActive(command.active());
        }
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteExercise(Long id) {
        Exercise exercise = getExercise(id);
        exerciseRepository.delete(exercise);
        logger.info("Deleted exercise {}", id);
    }
}
