package devkor.ontime_back.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FinishPreparationDto {
    private UUID scheduleId;
    private Integer latenessTime;
}
