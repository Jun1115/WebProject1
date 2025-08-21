package SINCREW.CrewBase.service;

import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.entity.UserEntity;
import SINCREW.CrewBase.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignUpService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(SignUpDTO signUpDTO) {

        String username = signUpDTO.getUsername();
        String password = signUpDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
