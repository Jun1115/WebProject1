package SINCREW.CrewBase.service;

import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.entity.UserEntity;
import SINCREW.CrewBase.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    // 의존성 주입
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignUpService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(SignUpDTO signUpDTO) {
        String username = signUpDTO.getUsername();
        String phone = signUpDTO.getPhone();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPhone(phone);
        data.setEmail(email);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_USER");

        userRepository.save(data);
    }
}
