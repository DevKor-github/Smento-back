package devkor.ontime_back.dto;

import lombok.Getter;

@Getter
public class OAuthAppleRequestDto {
    private String idToken;
    private String authCode;
    private String fullName;
    private String email;
}