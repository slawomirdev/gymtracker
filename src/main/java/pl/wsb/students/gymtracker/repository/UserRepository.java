package pl.wsb.students.gymtracker.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.students.gymtracker.domain.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
