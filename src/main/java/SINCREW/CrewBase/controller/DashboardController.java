package SINCREW.CrewBase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/dashboard";
    }
}
