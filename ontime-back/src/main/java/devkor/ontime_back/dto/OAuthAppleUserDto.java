package devkor.ontime_back.dto;

import lombok.Getter;

@Getter
public class OAuthAppleUserDto {
    private String appleUserId;
    private String email;
    private boolean isEmailVerified;
    private String fullName;

    public OAuthAppleUserDto(String appleUserId, String email, boolean isEmailVerified, String fullName) {
        this.appleUserId = appleUserId;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.fullName = fullName;
    }
}
