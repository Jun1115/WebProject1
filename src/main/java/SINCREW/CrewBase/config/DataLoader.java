package SINCREW.CrewBase.config;

import SINCREW.CrewBase.entity.UserEntity;
import SINCREW.CrewBase.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.save(new UserEntity(
                "rlawnstjr1115@naver.com",
                passwordEncoder.encode("dkffl123!"),
                "01049675820",
                "ROLE_USER",
                "김준석"
        ));
    }
}
