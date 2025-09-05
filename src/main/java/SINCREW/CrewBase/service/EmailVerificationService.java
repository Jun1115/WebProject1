package SINCREW.CrewBase.service;

import lombok.RequiredArgsConstructor;
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

    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_VALIDITY_SECONDS = 300; // 5분

    // 이메일로 인증 코드 전송
    public void sendVerificationEmail(String email) {
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
        return true;
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