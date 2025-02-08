package devkor.ontime_back.dto;

import lombok.Getter;

@Getter
public class OAuthGoogleRequestDto {
    private String accessToken;
    private String refreshToken;
}
