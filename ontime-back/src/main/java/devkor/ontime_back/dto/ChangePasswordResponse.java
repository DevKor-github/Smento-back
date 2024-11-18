package devkor.ontime_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordResponse {
    private boolean success;    // 성공 여부
    private String message;     // 사용자에게 전달할 메시지
}