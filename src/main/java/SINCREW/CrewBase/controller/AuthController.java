package SINCREW.CrewBase.controller;

import SINCREW.CrewBase.dto.EmailRequestDto;
import SINCREW.CrewBase.dto.VerificationRequestDto;
import SINCREW.CrewBase.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final EmailVerificationService emailVerificationService;

    // 인증 코드 전송 API
    @PostMapping("/api/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequestDto request) {
        emailVerificationService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    // 인증 코드 확인 API
    @PostMapping("/api/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequestDto request) {
        boolean isVerified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        if (isVerified) {
            return ResponseEntity.ok("인증이 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 유효하지 않거나 만료되었습니다.");
        }
    }
}