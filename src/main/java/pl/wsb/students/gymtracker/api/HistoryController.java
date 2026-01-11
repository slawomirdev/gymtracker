package pl.wsb.students.gymtracker.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wsb.students.gymtracker.api.dto.TrainingHistoryEntryResponse;
import pl.wsb.students.gymtracker.domain.TrainingSet;
import pl.wsb.students.gymtracker.service.TrainingSetService;
import pl.wsb.students.gymtracker.service.UserService;

@RestController
@RequestMapping("/api/exercises")
public class HistoryController {

    private final TrainingSetService trainingSetService;
    private final UserService userService;

    public HistoryController(TrainingSetService trainingSetService, UserService userService) {
        this.trainingSetService = trainingSetService;
        this.userService = userService;
    }

    @GetMapping("/{exerciseId}/history")
    public List<TrainingHistoryEntryResponse> history(@PathVariable Long exerciseId) {
        List<TrainingSet> history = trainingSetService.history(exerciseId, userService.getCurrentUserId());
        return history.stream()
                .map(set -> new TrainingHistoryEntryResponse(
                        set.getId(),
                        set.getTraining().getTrainingDate(),
                        set.getReps(),
                        set.getWeight()))
                .toList();
    }
}
