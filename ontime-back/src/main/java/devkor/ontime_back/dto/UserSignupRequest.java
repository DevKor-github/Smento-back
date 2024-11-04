package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@NoArgsConstructor
@Getter
public class UserSignupRequest {
    private String email;
    private Time spareTime;  // 여유시간
    private String note;     // 주의사항
    private Float score;     // 성실도 점수
}
