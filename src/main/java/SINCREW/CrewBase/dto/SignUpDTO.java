package SINCREW.CrewBase.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpDTO {

    @NotEmpty(message = "이름을 입력해주세요.")
    private String username;

    @NotEmpty(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "\\d+", message = "전화번호는 숫자만 입력해주세요.")
    private String phone;

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    @Pattern(
            regexp = "^[^><_'\"]*$",
            message = "비밀번호에 >, <, _, ', \" 문자는 사용할 수 없습니다."
    )
    private String password;
}