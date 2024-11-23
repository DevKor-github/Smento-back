package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Setter
    private Integer latenessTime; // 지각 시간 (NULL이면 약속 전, 0이면 약속 성공, N(양수)면 N분 지각)

    @Lob // 대용량 텍스트 필드
    @Column(columnDefinition = "TEXT") // 명시적으로 TEXT 타입으로 정의
    private String scheduleNote; // 스케줄 별 주의사항

    public void updateSchedule(Place place, String scheduleName, Time time, LocalDateTime scheduleTime, Time scheduleSpareTime, Integer latenessTime, String scheduleNote) {
        this.place = place;
        this.scheduleName = scheduleName;
        this.moveTime = time;
        this.scheduleTime = scheduleTime;
        this.scheduleSpareTime = scheduleSpareTime;
        this.latenessTime = latenessTime;
        this.scheduleNote = scheduleNote;
    }

    public void startSchedule() {
        this.isStarted = true;
    }

    public void changePreparationSchedule() {this.isChange = true;}
}


