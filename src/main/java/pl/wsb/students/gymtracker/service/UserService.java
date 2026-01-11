package pl.wsb.students.gymtracker.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.wsb.students.gymtracker.api.error.NotFoundException;
import pl.wsb.students.gymtracker.domain.AppUser;
import pl.wsb.students.gymtracker.repository.UserRepository;

@Service
public class UserService {

    private static final Long DEFAULT_USER_ID = 1L;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return userRepository.findById(DEFAULT_USER_ID)
                    .orElseThrow(() -> new NotFoundException("Default user not found"));
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
