package SINCREW.CrewBase.controller;

import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.service.SignUpService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignUpController {

    // 의존성 주입
    private final SignUpService signUpService;
    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String joinProcess(SignUpDTO signUpDTO, Model model) {

        try {
            signUpService.joinProcess(signUpDTO);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup"; // 다시 회원가입 페이지로
        }
    }
}
