package SINCREW.CrewBase.controller;

import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.service.EmailVerificationService; // EmailVerificationService 임포트 추가
import SINCREW.CrewBase.service.SignUpService;
import lombok.RequiredArgsConstructor; // Lombok 사용 시 추가
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Slf4j
public class SignUpController {

    private final SignUpService signUpService;
    private final EmailVerificationService emailVerificationService; // EmailVerificationService 주입

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String joinProcess(SignUpDTO signUpDTO, Model model) {

        log.info("회원가입 요청 이메일: {}", signUpDTO.getEmail());

        try {
            // ⭐ 이메일 인증 완료 상태 확인
            if (!emailVerificationService.isVerified(signUpDTO.getEmail())) {
                model.addAttribute("errorMessage", "이메일 인증을 먼저 완료해주세요.");
                return "signup"; // 인증 실패 시 다시 회원가입 페이지로
            }

            // 인증이 완료된 경우에만 회원가입 로직 진행
            signUpService.joinProcess(signUpDTO);

            // 회원가입 성공 후 Redis에서 인증 상태 삭제
            emailVerificationService.removeVerificationStatus(signUpDTO.getEmail());

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup"; // 서비스 로직에서 발생한 오류 처리
        }
    }
}