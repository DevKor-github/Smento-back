package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OAuthGoogleUserDto {

    private String sub;           // 고유 사용자 ID
    private String name;          // 사용자 이름
    private String givenName;     // 이름
    private String familyName;    // 성
    private String picture;       // 프로필 이미지 URL
    private String email;         // 이메일
    private boolean emailVerified; // 이메일 인증 여부

}
