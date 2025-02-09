package devkor.ontime_back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OAuthGoogleUserDto {

    private String id;           // 고유 사용자 ID
    private String name;          // 사용자 이름
    @JsonProperty("given_name") // JSON의 given_name 필드와 매핑
    private String givenName;
    @JsonProperty("family_name") // JSON의 family_name 필드와 매핑
    private String familyName;
    private String picture;       // 프로필 이미지 URL
    private String email;         // 이메일
    @JsonProperty("email_verified")
    private boolean emailVerified; // 이메일 인증 여부

}
