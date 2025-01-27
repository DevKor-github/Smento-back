package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
public class FinishPreparationDto {
    private UUID scheduleId;
    private Integer latenessTime;
}
