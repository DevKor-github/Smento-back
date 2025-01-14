package devkor.ontime_back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class UserSignUpDto {
    // 자체 로그인 회원 가입 API에 RequestBody로 사용할 UserSignUpDto를 생성
    private String email;
    private String password;
    private String name;
    private UUID userSettingId;

    @Builder
    public UserSignUpDto(String email, String password, String name, UUID userSettingId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userSettingId = userSettingId;
    }
}
