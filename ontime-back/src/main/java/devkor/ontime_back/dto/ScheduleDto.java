package devkor.ontime_back.dto;

import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가
public class ScheduleDto {
    private UUID scheduleId;
    private String placeName;
    private String scheduleName;
    private Integer moveTime;
    private LocalDateTime scheduleTime;
    private Integer scheduleSpareTime;
    private String scheduleNote;
    private Integer latenessTime;

}
