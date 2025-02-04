package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Time;

@ToString
@NoArgsConstructor
@Getter
public class SocialUserSignupDto {
    private Integer spareTime;  // 여유시간
    private String note;     // 주의사항
}
