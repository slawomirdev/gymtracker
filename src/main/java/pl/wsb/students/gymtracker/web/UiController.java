package pl.wsb.students.gymtracker.web;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wsb.students.gymtracker.api.dto.ExerciseRequest;
import pl.wsb.students.gymtracker.api.dto.TrainingCreateRequest;
import pl.wsb.students.gymtracker.api.dto.TrainingSetRequest;
import pl.wsb.students.gymtracker.service.ExerciseService;
import pl.wsb.students.gymtracker.service.StatsService;
import pl.wsb.students.gymtracker.service.TrainingService;
import pl.wsb.students.gymtracker.service.TrainingSetService;
import pl.wsb.students.gymtracker.service.UserService;

@Controller
public class UiController {

    private final ExerciseService exerciseService;
    private final TrainingService trainingService;
    private final TrainingSetService trainingSetService;
    private final UserService userService;
    private final StatsService statsService;

    public UiController(ExerciseService exerciseService,
                        TrainingService trainingService,
                        TrainingSetService trainingSetService,
                        UserService userService,
                        StatsService statsService) {
        this.exerciseService = exerciseService;
        this.trainingService = trainingService;
        this.trainingSetService = trainingSetService;
        this.userService = userService;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("exercises", exerciseService.listExercises());
        model.addAttribute("trainings", trainingService.listTrainings());
        return "index";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) Long exerciseId, Model model) {
        model.addAttribute("exercises", exerciseService.listExercises());
        model.addAttribute("selectedExerciseId", exerciseId);
        if (exerciseId != null) {
            model.addAttribute("history", trainingSetService.history(exerciseId, userService.getCurrentUserId()));
        }
        return "history";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("summary", statsService.summary());
        model.addAttribute("exerciseStats", statsService.exerciseStats());
        return "stats";
    }

    @GetMapping("/manage")
    public String manage(Model model) {
        model.addAttribute("exercises", exerciseService.listExercises());
        model.addAttribute("trainings", trainingService.listTrainings());
        return "manage";
    }

    @PostMapping("/ui/exercises")
    public String addExercise(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              RedirectAttributes redirectAttributes) {
        exerciseService.createExercise(new ExerciseRequest(name, description));
        redirectAttributes.addFlashAttribute("message", "Exercise added.");
        return "redirect:/";
    }

    @PostMapping("/ui/trainings")
    public String addTraining(@RequestParam String date,
                              @RequestParam(required = false) String note,
                              RedirectAttributes redirectAttributes) {
        trainingService.createTraining(new TrainingCreateRequest(java.time.LocalDate.parse(date), note));
        redirectAttributes.addFlashAttribute("message", "Training added.");
        return "redirect:/";
    }

    @PostMapping("/ui/exercises/{id}/update")
    public String updateExercise(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        exerciseService.updateExercise(id, new ExerciseRequest(name, description));
        redirectAttributes.addFlashAttribute("message", "Exercise updated.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/exercises/{id}/delete")
    public String deleteExercise(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        exerciseService.deleteExercise(id);
        redirectAttributes.addFlashAttribute("message", "Exercise deleted.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{id}/update")
    public String updateTraining(@PathVariable Long id,
                                 @RequestParam String date,
                                 @RequestParam(required = false) String note,
                                 RedirectAttributes redirectAttributes) {
        trainingService.updateTraining(id, new TrainingCreateRequest(java.time.LocalDate.parse(date), note));
        redirectAttributes.addFlashAttribute("message", "Training updated.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{id}/delete")
    public String deleteTraining(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        trainingService.deleteTraining(id);
        redirectAttributes.addFlashAttribute("message", "Training deleted.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{trainingId}/sets")
    public String addSet(@PathVariable Long trainingId,
                         @RequestParam Long exerciseId,
                         @RequestParam Integer reps,
                         @RequestParam BigDecimal weight,
                         RedirectAttributes redirectAttributes) {
        trainingSetService.addSet(trainingId, new TrainingSetRequest(exerciseId, reps, weight));
        redirectAttributes.addFlashAttribute("message", "Set added.");
        return "redirect:/";
    }

    @PostMapping("/ui/trainings/{trainingId}/sets/{setId}/delete")
    public String deleteSet(@PathVariable Long trainingId,
                            @PathVariable Long setId,
                            RedirectAttributes redirectAttributes) {
        trainingSetService.deleteSet(trainingId, setId, userService.getCurrentUserId());
        redirectAttributes.addFlashAttribute("message", "Set deleted.");
        return "redirect:/";
    }
}
