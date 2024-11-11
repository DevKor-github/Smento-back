package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;
import java.util.UUID;

@Getter
@Entity
public class PreparationSchedule {
    @Id
    private UUID preparationId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Time preparationTime;

    private Long sortNum;
}
