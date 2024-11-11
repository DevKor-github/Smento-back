package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
public class Schedule {

    @Id
    private UUID scheduleId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(nullable = false, length = 30)
    private String scheduleName;

    private Time moveTime; // 이동시간

    private LocalDateTime scheduleTime; // 약속시각

    private Boolean isChange; // 변경여부

    private Boolean isStarted; // 버튼누름여부

    private Time scheduleSpareTime; // 스케줄 별 여유시간

    @Lob // 대용량 텍스트 필드
    @Column(columnDefinition = "TEXT") // 명시적으로 TEXT 타입으로 정의
    private String scheduleNote; // 스케줄 별 주의사항

    public void updateSchedule(Place place, String scheduleName, Time time, LocalDateTime scheduleTime, Time scheduleSpareTime, String scheduleNote) {
        this.place = place;
        this.scheduleName = scheduleName;
        this.moveTime = time;
        this.scheduleTime = scheduleTime;
        this.scheduleSpareTime = scheduleSpareTime;
        this.scheduleNote = scheduleNote;
    }

    public void startSchedule() {
        this.isStarted = true;
    }
}


