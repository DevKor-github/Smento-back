package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;

@Getter
@Entity
public class PreparationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String preparationId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Time preparationTime;

    private Long order;
}
