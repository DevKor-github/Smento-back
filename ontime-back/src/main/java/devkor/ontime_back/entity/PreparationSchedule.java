package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Builder
public class PreparationSchedule {
    @Id
    private UUID preparationScheduleId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Integer preparationTime;

    @OneToOne
    @JoinColumn(name = "next_preparation_id")
    private PreparationSchedule nextPreparation;

    public PreparationSchedule(UUID preparationScheduleId, Schedule schedule, String preparationName, Integer preparationTime, PreparationSchedule preparationSchedule) {
        this.preparationScheduleId = preparationScheduleId;
        this.schedule = schedule;
        this.preparationName = preparationName;
        this.preparationTime = preparationTime;
        this.nextPreparation = preparationSchedule;
    }

    public void updateNextPreparation(PreparationSchedule nextPreparation) {
        this.nextPreparation = nextPreparation;
    }
}
