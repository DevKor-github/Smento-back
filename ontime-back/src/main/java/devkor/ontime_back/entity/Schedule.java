package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Entity
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

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

}

