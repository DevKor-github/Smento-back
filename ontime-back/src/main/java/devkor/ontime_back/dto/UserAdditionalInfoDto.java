package devkor.ontime_back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@AllArgsConstructor
@Getter
public class UserAdditionalInfoDto {
    private Integer spareTime;  // 여유시간
    private String note;     // 주의사항
}
