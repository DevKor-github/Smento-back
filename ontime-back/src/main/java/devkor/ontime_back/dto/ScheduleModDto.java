package devkor.ontime_back.dto;

import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ScheduleModDto {

    private UUID scheduleId;

    private UUID placeId;

    private String placeName;

    private String scheduleName;

    private Time moveTime; // 이동시간

    private LocalDateTime scheduleTime; // 약속시각

    private Integer scheduleSpareTime; // 스케줄 별 여유시간

    private Integer latenessTime;

    private String scheduleNote; // 스케줄 별 주의사항

}
