# GymTracker

Prosta aplikacja do planowania treningow i zapisywania serii. Backend to REST API na Spring Boot, dane trwale
w relacyjnej bazie PostgreSQL, a UI to lekki panel oparty o Bootstrap.

## Wymagania
- Java 21
- Maven
- PostgreSQL (lokalnie)

## Uruchomienie lokalne
1) Utworz baze danych:
```sql
create database gymtracker;
```
2) Upewnij sie, ze dane dostepu w `src/main/resources/application.properties` sa poprawne.
3) Uruchom aplikacje:
```bash
./mvnw spring-boot:run
```
4) Otworz UI: `http://localhost:8080`

## Uwierzytelnianie
Logowanie nie jest wymagane. UI i REST API sa dostepne bez autoryzacji.

## Glowne endpointy REST
- `GET /api/exercises` - lista cwiczen
- `POST /api/exercises` - tworzenie cwiczenia
- `GET /api/exercises/{id}` - szczegoly cwiczenia
- `PUT /api/exercises/{id}` - aktualizacja cwiczenia
- `DELETE /api/exercises/{id}` - usuniecie cwiczenia
- `POST /api/trainings` - utworzenie treningu
- `GET /api/trainings` - lista treningow
- `GET /api/trainings/{id}` - szczegoly treningu z seriami
- `PUT /api/trainings/{id}` - aktualizacja treningu (data, notatka)
- `POST /api/trainings/{id}/sets` - dodanie serii do treningu
- `DELETE /api/trainings/{trainingId}/sets/{setId}` - usuniecie serii
- `GET /api/exercises/{id}/history` - historia serii dla cwiczenia
- `GET /api/stats/summary` - podsumowanie statystyk
- `GET /api/stats/exercises` - statystyki per cwiczenie

## Swagger / OpenAPI
- UI: `http://localhost:8080/swagger-ui/index.html`
- Specyfikacja JSON: `http://localhost:8080/v3/api-docs`

## Widoki UI
- `/` - dashboard (dodawanie, lista treningow i serii)
- `/history` - historia serii dla wybranego cwiczenia
- `/stats` - podsumowanie i statystyki cwiczen
- `/manage` - edycja i usuwanie cwiczen oraz treningow

## Przykladowe requesty
```bash
curl -u demo:demo -H "Content-Type: application/json" \
  -d '{"name":"Przysiad","description":"Stabilizacja core"}' \
  http://localhost:8080/api/exercises

curl -u demo:demo -H "Content-Type: application/json" \
  -d '{"date":"2025-01-10","note":"Sila + mobilnosc"}' \
  http://localhost:8080/api/trainings
```

## Testy
```bash
./mvnw test
```
