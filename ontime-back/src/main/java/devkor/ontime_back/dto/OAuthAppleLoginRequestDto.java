package devkor.ontime_back.dto;

import lombok.Getter;

@Getter
public class OAuthAppleLoginRequestDto {
    private String idToken;
    private String authCode;
    private String fullName;
}
