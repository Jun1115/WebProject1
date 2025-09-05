package SINCREW.CrewBase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 인증 코드 확인 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequestDto {
    private String email;
    private String code;
}