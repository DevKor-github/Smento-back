package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class PreparationSchedule {
    @Id
    private UUID preparationId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Integer preparationTime;

    @ManyToOne
    @JoinColumn(name = "next_preparation_id")
    private PreparationSchedule nextPreparation;


    public PreparationSchedule(UUID preparationId, Schedule schedule, String preparationName, Integer preparationTime, PreparationSchedule preparationSchedule) {
        this.preparationId = preparationId;
        this.schedule = schedule;
        this.preparationName = preparationName;
        this.preparationTime = preparationTime;
        this.nextPreparation = preparationSchedule;
    }

    public void updateNextPreparation(PreparationSchedule nextPreparation) {
        this.nextPreparation = nextPreparation;
    }
}
