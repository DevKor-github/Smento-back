package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class UserSignUpDto {
    // 자체 로그인 회원 가입 API에 RequestBody로 사용할 UserSignUpDto를 생성

    private Long id; //uuid형식으로 옴. db에 넣을 때는 uuid로 변환해야함
    private String email;
    private String password;
    private String name;
}
