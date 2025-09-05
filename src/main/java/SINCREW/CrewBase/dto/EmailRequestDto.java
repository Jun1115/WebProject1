package SINCREW.CrewBase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


// 이메일 전송 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {
    private String email;
}
