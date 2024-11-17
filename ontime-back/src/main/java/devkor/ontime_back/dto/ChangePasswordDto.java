package devkor.ontime_back.dto;

import lombok.Getter;

@Getter
public class ChangePasswordDto {
    private String currentPassword;
    private String newPassword;
}
