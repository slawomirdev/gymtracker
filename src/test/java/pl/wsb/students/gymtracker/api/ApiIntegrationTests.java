package pl.wsb.students.gymtracker.api;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpResponse;
import pl.wsb.students.gymtracker.repository.ExerciseRepository;
import pl.wsb.students.gymtracker.repository.TrainingRepository;
import pl.wsb.students.gymtracker.repository.TrainingSetRepository;
import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApiIntegrationTests {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrainingSetRepository trainingSetRepository;

    @BeforeEach
    void resetData() {
        trainingSetRepository.deleteAll();
        trainingRepository.deleteAll();
        exerciseRepository.deleteAll();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    @Test
    void createsAndListsExercise() throws Exception {
        ExercisePayload payload = new ExercisePayload("Przysiad", "Technika i oddech");
        ResponseEntity<ExerciseResponse> created = restTemplate.postForEntity(
                url("/api/exercises"), payload, ExerciseResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().id()).isNotNull();
        assertThat(created.getBody().name()).isEqualTo("Przysiad");

        ResponseEntity<List<ExerciseResponse>> listResponse = restTemplate.exchange(
                RequestEntity.get(url("/api/exercises")).build(),
                new ParameterizedTypeReference<>() {});
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotNull();
        assertThat(listResponse.getBody().get(0).name()).isEqualTo("Przysiad");
    }

    @Test
    void createsTrainingAndAddsSet() throws Exception {
        Long exerciseId = createExercise("Martwy ciag");
        Long trainingId = createTraining(LocalDate.now());

        TrainingSetPayload setPayload = new TrainingSetPayload(exerciseId, 5, new BigDecimal("120.5"));

        ResponseEntity<TrainingSetResponse> created = restTemplate.postForEntity(
                url("/api/trainings/%d/sets".formatted(trainingId)), setPayload, TrainingSetResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().exerciseId()).isEqualTo(exerciseId);
        assertThat(created.getBody().reps()).isEqualTo(5);
    }

    @Test
    void returnsHistoryForExercise() throws Exception {
        Long exerciseId = createExercise("Wyciskanie");
        Long trainingId = createTraining(LocalDate.now().minusDays(1));

        TrainingSetPayload setPayload = new TrainingSetPayload(exerciseId, 8, new BigDecimal("80"));

        ResponseEntity<TrainingSetResponse> created = restTemplate.postForEntity(
                url("/api/trainings/%d/sets".formatted(trainingId)), setPayload, TrainingSetResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<List<TrainingHistoryEntryResponse>> history = restTemplate.exchange(
                RequestEntity.get(url("/api/exercises/%d/history".formatted(exerciseId))).build(),
                new ParameterizedTypeReference<>() {});
        assertThat(history.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(history.getBody()).isNotNull();
        assertThat(history.getBody().get(0).reps()).isEqualTo(8);
        assertThat(history.getBody().get(0).weight().compareTo(new BigDecimal("80"))).isZero();
    }

    @Test
    void returnsValidationErrorFormat() throws Exception {
        ExercisePayload payload = new ExercisePayload("", "x");
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/exercises"), payload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiErrorResponse error = objectMapper.readValue(response.getBody(), ApiErrorResponse.class);
        assertThat(error.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(error.details().get(0).field()).isEqualTo("name");
    }

    private Long createExercise(String name) throws Exception {
        ExercisePayload payload = new ExercisePayload(name, "Test");
        ResponseEntity<ExerciseResponse> response = restTemplate.postForEntity(
                url("/api/exercises"), payload, ExerciseResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        return response.getBody().id();
    }

    private Long createTraining(LocalDate date) throws Exception {
        TrainingPayload payload = new TrainingPayload(date.toString(), "Test");
        ResponseEntity<TrainingResponse> response = restTemplate.postForEntity(
                url("/api/trainings"), payload, TrainingResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        return response.getBody().id();
    }

    private record ExercisePayload(String name, String description) {
    }

    private record TrainingPayload(String date, String note) {
    }

    private record TrainingSetPayload(Long exerciseId, Integer reps, BigDecimal weight) {
    }

    private record ExerciseResponse(Long id, String name, String description, String imageUrl, Boolean active) {
    }

    private record TrainingResponse(Long id, String date, String note, String intensity, String location,
                                   String bodyWeight, Integer durationMinutes, List<TrainingSetResponse> sets) {
    }

    private record TrainingSetResponse(Long id, Long exerciseId, String exerciseName, Integer reps, BigDecimal weight) {
    }

    private record TrainingHistoryEntryResponse(Long setId, String trainingDate, Integer reps, BigDecimal weight) {
    }

    private record ApiErrorResponse(String code, String message, String timestamp, List<FieldError> details) {
    }

    private record FieldError(String field, String message) {
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
