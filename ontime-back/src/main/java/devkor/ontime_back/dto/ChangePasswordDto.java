package devkor.ontime_back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePasswordDto {
    private String currentPassword;
    private String newPassword;
}
