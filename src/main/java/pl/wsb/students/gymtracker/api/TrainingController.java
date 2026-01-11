package pl.wsb.students.gymtracker.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.wsb.students.gymtracker.api.dto.TrainingCreateRequest;
import pl.wsb.students.gymtracker.api.dto.TrainingResponse;
import pl.wsb.students.gymtracker.api.dto.TrainingSetRequest;
import pl.wsb.students.gymtracker.api.dto.TrainingSetResponse;
import pl.wsb.students.gymtracker.domain.Training;
import pl.wsb.students.gymtracker.domain.TrainingSet;
import pl.wsb.students.gymtracker.service.TrainingService;
import pl.wsb.students.gymtracker.service.TrainingSetService;
import pl.wsb.students.gymtracker.service.UserService;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingSetService trainingSetService;
    private final UserService userService;

    public TrainingController(TrainingService trainingService,
                              TrainingSetService trainingSetService,
                              UserService userService) {
        this.trainingService = trainingService;
        this.trainingSetService = trainingSetService;
        this.userService = userService;
    }

    @GetMapping
    public List<TrainingResponse> list() {
        return trainingService.listTrainings().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<TrainingResponse> create(@Valid @RequestBody TrainingCreateRequest request) {
        Training created = trainingService.createTraining(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(created));
    }

    @GetMapping("/{id}")
    public TrainingResponse get(@PathVariable Long id) {
        return toResponse(trainingService.getTraining(id));
    }

    @PostMapping("/{id}/sets")
    public ResponseEntity<TrainingSetResponse> addSet(@PathVariable Long id,
                                                      @Valid @RequestBody TrainingSetRequest request) {
        TrainingSet created = trainingSetService.addSet(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{setId}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(toSetResponse(created));
    }

    @DeleteMapping("/{trainingId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long trainingId, @PathVariable Long setId) {
        trainingSetService.deleteSet(trainingId, setId, userService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    private TrainingResponse toResponse(Training training) {
        List<TrainingSetResponse> sets = training.getSets().stream()
                .map(this::toSetResponse)
                .toList();
        return new TrainingResponse(training.getId(), training.getTrainingDate(), training.getNote(), sets);
    }

    private TrainingSetResponse toSetResponse(TrainingSet set) {
        return new TrainingSetResponse(
                set.getId(),
                set.getExercise().getId(),
                set.getExercise().getName(),
                set.getReps(),
                set.getWeight()
        );
    }
}
