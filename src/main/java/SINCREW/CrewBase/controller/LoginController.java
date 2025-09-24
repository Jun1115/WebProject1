package SINCREW.CrewBase.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        // 세션에서 오류 메시지를 가져옴
        String errorMessage = (String) session.getAttribute("loginError");

        // 메시지가 존재하면 모델에 추가
        if (errorMessage != null) {
            model.addAttribute("loginError", errorMessage);
            // 메시지를 한 번 사용한 후 세션에서 즉시 제거
            session.removeAttribute("loginError");
        }

        return "login";
    }
}
