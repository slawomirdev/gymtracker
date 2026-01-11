package pl.wsb.students.gymtracker.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.wsb.students.gymtracker.api.dto.TrainingCreateRequest;
import pl.wsb.students.gymtracker.api.error.NotFoundException;
import pl.wsb.students.gymtracker.domain.AppUser;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.repository.TrainingRepository;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final UserService userService;

    public TrainingService(TrainingRepository trainingRepository, UserService userService) {
        this.trainingRepository = trainingRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Training> listTrainings() {
        AppUser user = userService.getCurrentUser();
        return trainingRepository.findAllByUserIdOrderByTrainingDateDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Training> listTrainingsLimited(int limit) {
        AppUser user = userService.getCurrentUser();
        int safeLimit = Math.max(1, Math.min(limit, 50));
        var page = PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "trainingDate"));
        return trainingRepository.findByUserIdOrderByTrainingDateDesc(user.getId(), page).getContent();
    }

    @Transactional(readOnly = true)
    public Training getTraining(Long id) {
        AppUser user = userService.getCurrentUser();
        return trainingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Training not found"));
    }

    @Transactional
    public Training createTraining(TrainingCreateRequest request) {
        AppUser user = userService.getCurrentUser();
        Training training = new Training();
        training.setUser(user);
        training.setTrainingDate(request.date());
        training.setNote(request.note());
        Training saved = trainingRepository.save(training);
        logger.info("Created training {} for user {}", saved.getId(), user.getId());
        return saved;
    }

    @Transactional
    public Training updateTraining(Long id, TrainingCreateRequest request) {
        Training training = getTraining(id);
        training.setTrainingDate(request.date());
        training.setNote(request.note());
        return trainingRepository.save(training);
    }

    @Transactional
    public void deleteTraining(Long id) {
        Training training = getTraining(id);
        trainingRepository.delete(training);
        logger.info("Deleted training {}", id);
    }
}
