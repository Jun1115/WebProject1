package SINCREW.CrewBase.controller;

import SINCREW.CrewBase.dto.EmailRequestDto;
import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.dto.VerificationRequestDto;
import SINCREW.CrewBase.repository.UserRepository;
import SINCREW.CrewBase.service.EmailVerificationService; // EmailVerificationService 임포트 추가
import SINCREW.CrewBase.service.SignUpService;
import lombok.RequiredArgsConstructor; // Lombok 사용 시 추가
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@RequestMapping("/member")
public class MemberController {

    private final SignUpService signUpService;
    private final EmailVerificationService emailVerificationService; // EmailVerificationService 주입
    private final UserRepository userRepository;

//  1단계 회원가입 약관동의
    @GetMapping("/signUpAgree")
    public String signUpAgree() {
        return "member/signUpAgree";
    }

    @PostMapping("/signUpAgree")
    public String handleTermsAgreement(@RequestParam("serviceTermsAgreed") boolean serviceTermsAgreed,
                                       @RequestParam("privacyPolicyAgreed") boolean privacyPolicyAgreed,
                                       @RequestParam(value = "marketingAgreed", defaultValue = "false") boolean marketingAgreed,
                                       RedirectAttributes redirectAttributes) {

        // 서버에서 필수 약관 동의 여부 재확인 (보안)
        if (!serviceTermsAgreed || !privacyPolicyAgreed) {
            // 필수 약관 동의가 안된 경우 처리
            // 예: 다시 약관 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", "필수 약관에 동의해야 합니다.");
            return "redirect:/member/signUpAgree"; // 약관 동의 페이지 URL
        }

        // 모든 조건이 충족되면 다음 단계(회원정보 입력) 페이지로 이동
        return "redirect:/member/signUp"; // 회원정보 입력 페이지 URL
    }

//  2단계 회원정보 입력
    @GetMapping("/signUp")
    public String signUp() {
        return "member/signUp";
    }

    @PostMapping("/signUp")
    public String joinProcess(SignUpDTO signUpDTO, Model model) {

        try {
            // ⭐ 이메일 인증 완료 상태 확인
            System.out.println("회원가입 요청 시작: " + signUpDTO.getEmail()); // ⭐ 로그 추가

            // ⭐ 이메일 인증 완료 상태 확인
            if (!emailVerificationService.isVerified(signUpDTO.getEmail())) {
                System.out.println("이메일 인증 실패: " + signUpDTO.getEmail()); // ⭐ 로그 추가
                model.addAttribute("errorMessage", "이메일 인증을 먼저 완료해주세요.");
                return "member/signUp";
            }

            System.out.println("이메일 인증 성공. 회원가입 진행."); // ⭐ 로그 추가
            // 인증이 완료된 경우에만 회원가입 로직 진행
            signUpService.joinProcess(signUpDTO);

            // 회원가입 성공 후 Redis에서 인증 상태 삭제
            emailVerificationService.removeVerificationStatus(signUpDTO.getEmail());

            return "redirect:/member/signUpComplete";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signUp"; // 서비스 로직에서 발생한 오류 처리
        }
    }


    // 인증 코드 전송 API
    @PostMapping("/api/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequestDto request) {
        // ⭐ 중복 체크 추가
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

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

//  3단계 회원가입 완료
    @GetMapping("/signUpComplete")
    public String signUpComplete() {
        return "member/signUpComplete";
    }
}