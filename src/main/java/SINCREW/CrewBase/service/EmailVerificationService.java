package SINCREW.CrewBase.service;

import SINCREW.CrewBase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromAddress;

    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private static final String VERIFIED_PREFIX = "Verified "; // 인증 완료 상태를 저장할 접두사
    private static final int CODE_LENGTH = 6;
    private static final long CODE_VALIDITY_SECONDS = 300; // 5분
    private static final long VERIFIED_VALIDITY_SECONDS = 600; // 인증 완료 상태 유효 시간 (10분)

    // 이메일로 인증 코드 전송
    public void sendVerificationEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 1. 랜덤 인증 코드 생성
        String authCode = generateRandomCode();

        // 2. Redis에 이메일과 인증 코드 저장 (유효 기간 5분)
        redisTemplate.opsForValue().set(
                AUTH_CODE_PREFIX + email,
                authCode,
                CODE_VALIDITY_SECONDS,
                TimeUnit.SECONDS
        );

        // 3. 이메일 내용 구성 및 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("[회원가입] 이메일 인증 코드입니다.");
        message.setText("인증 코드: " + authCode + "\n\n제한 시간: 5분");
        mailSender.send(message);
    }

    // 인증 코드 유효성 검증
    public boolean verifyCode(String email, String code) {
        // 1. Redis에서 저장된 인증 코드 가져오기
        String redisKey = AUTH_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        // 2. 저장된 코드가 없거나, 사용자가 입력한 코드와 다르면 실패
        if (storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        // 3. 인증 성공 시, Redis에서 인증 코드 삭제 (재사용 방지)
        redisTemplate.delete(redisKey);
        // ⭐ 4. 인증 완료 상태를 Redis에 저장
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", VERIFIED_VALIDITY_SECONDS, TimeUnit.SECONDS);
        return true;
    }

    // ⭐ 인증 완료 상태를 확인하는 메서드 추가
    public boolean isVerified(String email) {
        String redisKey = VERIFIED_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    // ⭐ 회원가입 후 인증 완료 상태를 삭제하는 메서드 추가
    public void removeVerificationStatus(String email) {
        redisTemplate.delete(VERIFIED_PREFIX + email);
    }

    // 6자리 랜덤 인증 코드 생성
    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}