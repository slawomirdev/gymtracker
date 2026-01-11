package pl.wsb.students.gymtracker.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wsb.students.gymtracker.api.dto.ExerciseStatsResponse;
import pl.wsb.students.gymtracker.api.dto.StatsSummaryResponse;
import pl.wsb.students.gymtracker.service.StatsService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/summary")
    public StatsSummaryResponse summary() {
        return statsService.summary();
    }

    @GetMapping("/exercises")
    public List<ExerciseStatsResponse> exercises() {
        return statsService.exerciseStats();
    }
}
