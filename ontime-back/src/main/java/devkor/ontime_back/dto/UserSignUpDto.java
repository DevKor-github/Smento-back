package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {
    // 자체 로그인 회원 가입 API에 RequestBody로 사용할 UserSignUpDto를 생성

    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;
}
