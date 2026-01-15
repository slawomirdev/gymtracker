package pl.wsb.students.gymtracker.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wsb.students.gymtracker.service.ExerciseService;
import pl.wsb.students.gymtracker.service.StatsService;
import pl.wsb.students.gymtracker.service.TrainingService;
import pl.wsb.students.gymtracker.service.TrainingSetService;
import pl.wsb.students.gymtracker.service.UserService;
import pl.wsb.students.gymtracker.service.dto.ExerciseCommand;
import pl.wsb.students.gymtracker.service.dto.TrainingCommand;
import pl.wsb.students.gymtracker.service.dto.TrainingSetCommand;

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
    public String index(@RequestParam(defaultValue = "5") int limit, Model model) {
        model.addAttribute("exercises", exerciseService.listExercises());
        model.addAttribute("activeExercises", exerciseService.listActiveExercises());
        model.addAttribute("trainings", trainingService.listTrainingsLimited(limit));
        model.addAttribute("trainingLimit", limit);
        model.addAttribute("trainingNextLimit", Math.min(limit + 5, 50));
        model.addAttribute("summary", statsService.summary());
        return "index";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) Long exerciseId,
                          @RequestParam(defaultValue = "20") int limit,
                          Model model) {
        model.addAttribute("exercises", exerciseService.listExercises());
        model.addAttribute("selectedExerciseId", exerciseId);
        model.addAttribute("historyLimit", limit);
        model.addAttribute("historyNextLimit", Math.min(limit + 20, 100));
        if (exerciseId != null) {
            model.addAttribute("history", trainingSetService.history(
                    exerciseId, userService.getCurrentUserId(), limit));
        }
        return "history";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        var summary = statsService.summary();
        var exerciseStats = statsService.exerciseStats();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> volumes = new ArrayList<>();
        List<BigDecimal> maxWeights = new ArrayList<>();
        for (var row : exerciseStats) {
            labels.add(row.exerciseName());
            volumes.add(row.totalVolume());
            maxWeights.add(row.maxWeight());
        }
        model.addAttribute("summary", summary);
        model.addAttribute("exerciseStats", exerciseStats);
        model.addAttribute("statsLabels", labels);
        model.addAttribute("statsVolumes", volumes);
        model.addAttribute("statsMaxWeights", maxWeights);
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
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam(defaultValue = "false") boolean active,
                              RedirectAttributes redirectAttributes) {
        String trimmedName = emptyToNull(name);
        if (trimmedName == null) {
            redirectAttributes.addFlashAttribute("error", "Nazwa cwiczenia jest wymagana.");
            return "redirect:/";
        }
        if (trimmedName.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Nazwa cwiczenia jest za dluga (max 100).");
            return "redirect:/";
        }
        String trimmedDescription = emptyToNull(description);
        if (trimmedDescription != null && trimmedDescription.length() > 500) {
            redirectAttributes.addFlashAttribute("error", "Opis cwiczenia jest za dlugi (max 500).");
            return "redirect:/";
        }
        String trimmedImageUrl = emptyToNull(imageUrl);
        if (trimmedImageUrl != null && trimmedImageUrl.length() > 300) {
            redirectAttributes.addFlashAttribute("error", "Link do zdjecia jest za dlugi (max 300).");
            return "redirect:/";
        }
        exerciseService.createExercise(new ExerciseCommand(trimmedName, trimmedDescription, trimmedImageUrl, active));
        redirectAttributes.addFlashAttribute("message", "Cwiczenie dodane.");
        return "redirect:/";
    }

    @PostMapping("/ui/trainings")
    public String addTraining(@RequestParam String date,
                              @RequestParam(required = false) String note,
                              @RequestParam(required = false) String intensity,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) String bodyWeight,
                              @RequestParam(required = false) String durationMinutes,
                              RedirectAttributes redirectAttributes) {
        if (isBlank(date)) {
            redirectAttributes.addFlashAttribute("error", "Data treningu jest wymagana.");
            return "redirect:/";
        }
        java.time.LocalDate trainingDate;
        try {
            trainingDate = java.time.LocalDate.parse(date.trim());
        } catch (java.time.format.DateTimeParseException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawny format daty treningu.");
            return "redirect:/";
        }
        java.math.BigDecimal parsedBodyWeight;
        try {
            parsedBodyWeight = parseBigDecimal(bodyWeight);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawna waga ciala.");
            return "redirect:/";
        }
        if (parsedBodyWeight != null && parsedBodyWeight.compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("error", "Waga ciala musi byc >= 0.");
            return "redirect:/";
        }
        Integer parsedDuration;
        try {
            parsedDuration = parseInteger(durationMinutes);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawny czas trwania.");
            return "redirect:/";
        }
        if (parsedDuration != null && parsedDuration <= 0) {
            redirectAttributes.addFlashAttribute("error", "Czas trwania musi byc > 0.");
            return "redirect:/";
        }
        trainingService.createTraining(new TrainingCommand(
                trainingDate,
                emptyToNull(note),
                emptyToNull(intensity),
                emptyToNull(location),
                parsedBodyWeight,
                parsedDuration
        ));
        redirectAttributes.addFlashAttribute("message", "Trening dodany.");
        return "redirect:/";
    }

    @PostMapping("/ui/exercises/{id}/update")
    public String updateExercise(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) String imageUrl,
                                 @RequestParam(defaultValue = "false") boolean active,
                                 RedirectAttributes redirectAttributes) {
        String trimmedName = emptyToNull(name);
        if (trimmedName == null) {
            redirectAttributes.addFlashAttribute("error", "Nazwa cwiczenia jest wymagana.");
            return "redirect:/manage";
        }
        if (trimmedName.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Nazwa cwiczenia jest za dluga (max 100).");
            return "redirect:/manage";
        }
        String trimmedDescription = emptyToNull(description);
        if (trimmedDescription != null && trimmedDescription.length() > 500) {
            redirectAttributes.addFlashAttribute("error", "Opis cwiczenia jest za dlugi (max 500).");
            return "redirect:/manage";
        }
        String trimmedImageUrl = emptyToNull(imageUrl);
        if (trimmedImageUrl != null && trimmedImageUrl.length() > 300) {
            redirectAttributes.addFlashAttribute("error", "Link do zdjecia jest za dlugi (max 300).");
            return "redirect:/manage";
        }
        exerciseService.updateExercise(id, new ExerciseCommand(trimmedName, trimmedDescription, trimmedImageUrl, active));
        redirectAttributes.addFlashAttribute("message", "Cwiczenie zaktualizowane.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/exercises/{id}/delete")
    public String deleteExercise(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        exerciseService.deleteExercise(id);
        redirectAttributes.addFlashAttribute("message", "Cwiczenie usuniete.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{id}/update")
    public String updateTraining(@PathVariable Long id,
                                 @RequestParam String date,
                                 @RequestParam(required = false) String note,
                                 @RequestParam(required = false) String intensity,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String bodyWeight,
                                 @RequestParam(required = false) String durationMinutes,
                                 RedirectAttributes redirectAttributes) {
        if (isBlank(date)) {
            redirectAttributes.addFlashAttribute("error", "Data treningu jest wymagana.");
            return "redirect:/manage";
        }
        java.time.LocalDate trainingDate;
        try {
            trainingDate = java.time.LocalDate.parse(date.trim());
        } catch (java.time.format.DateTimeParseException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawny format daty treningu.");
            return "redirect:/manage";
        }
        java.math.BigDecimal parsedBodyWeight;
        try {
            parsedBodyWeight = parseBigDecimal(bodyWeight);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawna waga ciala.");
            return "redirect:/manage";
        }
        if (parsedBodyWeight != null && parsedBodyWeight.compareTo(java.math.BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("error", "Waga ciala musi byc >= 0.");
            return "redirect:/manage";
        }
        Integer parsedDuration;
        try {
            parsedDuration = parseInteger(durationMinutes);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawny czas trwania.");
            return "redirect:/manage";
        }
        if (parsedDuration != null && parsedDuration <= 0) {
            redirectAttributes.addFlashAttribute("error", "Czas trwania musi byc > 0.");
            return "redirect:/manage";
        }
        trainingService.updateTraining(id, new TrainingCommand(
                trainingDate,
                emptyToNull(note),
                emptyToNull(intensity),
                emptyToNull(location),
                parsedBodyWeight,
                parsedDuration
        ));
        redirectAttributes.addFlashAttribute("message", "Trening zaktualizowany.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{id}/delete")
    public String deleteTraining(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        trainingService.deleteTraining(id);
        redirectAttributes.addFlashAttribute("message", "Trening usuniety.");
        return "redirect:/manage";
    }

    @PostMapping("/ui/trainings/{trainingId}/sets")
    public String addSet(@PathVariable Long trainingId,
                         @RequestParam Long exerciseId,
                         @RequestParam Integer reps,
                         @RequestParam BigDecimal weight,
                         RedirectAttributes redirectAttributes) {
        if (exerciseId == null) {
            redirectAttributes.addFlashAttribute("error", "Wybierz cwiczenie.");
            return "redirect:/";
        }
        if (reps == null || reps <= 0) {
            redirectAttributes.addFlashAttribute("error", "Liczba powtorzen musi byc > 0.");
            return "redirect:/";
        }
        if (weight == null || weight.compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("error", "Ciezar musi byc >= 0.");
            return "redirect:/";
        }
        trainingSetService.addSet(trainingId, new TrainingSetCommand(exerciseId, reps, weight));
        redirectAttributes.addFlashAttribute("message", "Seria dodana.");
        return "redirect:/";
    }

    @PostMapping("/ui/trainings/{trainingId}/sets/{setId}/delete")
    public String deleteSet(@PathVariable Long trainingId,
                            @PathVariable Long setId,
                            RedirectAttributes redirectAttributes) {
        trainingSetService.deleteSet(trainingId, setId, userService.getCurrentUserId());
        redirectAttributes.addFlashAttribute("message", "Seria usunieta.");
        return "redirect:/";
    }

    @PostMapping("/ui/exercises/bulk")
    public String addExercisesBulk(@ModelAttribute BulkExerciseForm bulkForm,
                                   @RequestParam(required = false) String bulkLines,
                                   RedirectAttributes redirectAttributes) {
        List<ExerciseCommand> requests = new ArrayList<>();
        requests.addAll(buildFromForm(bulkForm));
        if (requests.isEmpty() && bulkLines != null) {
            requests.addAll(buildFromLegacyLines(bulkLines));
        }
        if (requests.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono poprawnych cwiczen do dodania.");
            return "redirect:/manage";
        }
        for (ExerciseCommand request : requests) {
            exerciseService.createExercise(request);
        }
        redirectAttributes.addFlashAttribute("message", "Dodano cwiczenia: " + requests.size() + ".");
        return "redirect:/manage";
    }

    private java.math.BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        return new java.math.BigDecimal(raw.replace(",", ".").trim());
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(raw.trim());
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean parseBoolean(String raw) {
        if (raw == null) {
            return true;
        }
        String value = raw.trim().toLowerCase();
        return !(value.equals("false") || value.equals("0") || value.equals("nie"));
    }

    private List<ExerciseCommand> buildFromForm(BulkExerciseForm bulkForm) {
        List<ExerciseCommand> requests = new ArrayList<>();
        if (bulkForm == null || bulkForm.getExercises() == null) {
            return requests;
        }
        for (BulkExerciseRow row : bulkForm.getExercises()) {
            if (row == null) {
                continue;
            }
            String name = emptyToNull(row.getName());
            if (name == null) {
                continue;
            }
            String description = emptyToNull(row.getDescription());
            String imageUrl = emptyToNull(row.getImageUrl());
            boolean active = row.getActive() == null || row.getActive();
            requests.add(new ExerciseCommand(name, description, imageUrl, active));
        }
        return requests;
    }

    private List<ExerciseCommand> buildFromLegacyLines(String bulkLines) {
        List<ExerciseCommand> requests = new ArrayList<>();
        if (bulkLines == null) {
            return requests;
        }
        String[] lines = bulkLines.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] parts = trimmed.split(";", -1);
            String name = parts[0].trim();
            if (name.isEmpty()) {
                continue;
            }
            String description = parts.length > 1 ? emptyToNull(parts[1]) : null;
            String imageUrl = parts.length > 2 ? emptyToNull(parts[2]) : null;
            Boolean active = parts.length > 3 ? parseBoolean(parts[3]) : true;
            requests.add(new ExerciseCommand(name, description, imageUrl, active));
        }
        return requests;
    }

    public static class BulkExerciseForm {
        private List<BulkExerciseRow> exercises = new ArrayList<>();

        public List<BulkExerciseRow> getExercises() {
            return exercises;
        }

        public void setExercises(List<BulkExerciseRow> exercises) {
            this.exercises = exercises;
        }
    }

    public static class BulkExerciseRow {
        private String name;
        private String description;
        private String imageUrl;
        private Boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
