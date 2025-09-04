package SINCREW.CrewBase.service;

import SINCREW.CrewBase.dto.CustomUserDetails;
import SINCREW.CrewBase.entity.UserEntity;
import SINCREW.CrewBase.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        // 이메일로 먼저 찾고
        UserEntity userData = userRepository.findByPhone(identifier);

        // 없으면 전화번호로 찾고
        if (userData == null) {
            userData = userRepository.findByEmail(identifier);
        }
        if (userData == null) {
            throw new UsernameNotFoundException("사용자 없음");
        }

        return new CustomUserDetails(userData);
    }
}