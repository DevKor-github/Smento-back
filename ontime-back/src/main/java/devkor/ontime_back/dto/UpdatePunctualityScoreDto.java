package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class UpdatePunctualityScoreDto {
    private Long userId;
    private Integer latenessTime;
}
