package SINCREW.CrewBase.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpDTO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}

// @Valid 어노테이션 쓰려고 했는데 클라이언트 입장에서 생각해보면 입력할 때 즉시 피드백이 있는게 좋지 않을 까 싶음
// 꾸역꾸역 다 입력하고 회원가입 눌렀을 때 피드백을 와다다 주면 귀찮을 것 같음
// 고로 html, js를 사용하는게 낫다고 판단하겠습니다.