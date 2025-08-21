package SINCREW.CrewBase.controller;

import SINCREW.CrewBase.dto.SignUpDTO;
import SINCREW.CrewBase.service.SignUpService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignUpController {

    private final SignUpService signUpService;

    public SignUpController(SignUpService signUpService) {

        this.signUpService = signUpService;
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signUp")
    public String joinProcess(SignUpDTO signUpDTO) {

        System.out.println(signUpDTO.getUsername());
        signUpService.joinProcess(signUpDTO);

        return "ok";
    }
}
