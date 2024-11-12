package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@NoArgsConstructor
@Getter
public class UserAdditionalInfoDto {
    private Time spareTime;  // 여유시간
    private String note;     // 주의사항
}
