package devkor.ontime_back.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ScheduleHistoryResponse {
    private UUID scheduleId;
    private String scheduleName;
    private LocalDateTime scheduleTime;
    private int latenessTime;

    public ScheduleHistoryResponse(UUID scheduleId, String scheduleName, LocalDateTime scheduleTime, int latenessTime) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.latenessTime = latenessTime;
    }
}