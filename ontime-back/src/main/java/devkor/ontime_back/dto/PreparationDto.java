package devkor.ontime_back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Time;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class PreparationDto {
    private UUID preparationId;

    private String preparationName;

    private Integer preparationTime;

    private UUID nextPreparationId;

    @Override
    public String toString() {
        return "PreparationDto{" +
                "preparationId=" + preparationId +
                ", preparationName='" + preparationName + '\'' +
                ", preparationTime=" + preparationTime +
                ", nextPreparationId=" + nextPreparationId +
                '}';
    }
}
