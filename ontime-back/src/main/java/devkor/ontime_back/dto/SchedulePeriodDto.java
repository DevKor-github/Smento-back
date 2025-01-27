package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class SchedulePeriodDto {
    LocalDateTime startDate;
    LocalDateTime endDate;
}
