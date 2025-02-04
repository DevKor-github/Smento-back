package devkor.ontime_back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class UpdateSpareTimeDto {
    private Integer newSpareTime;

    @Builder
    public UpdateSpareTimeDto(Integer newSpareTime) {
        this.newSpareTime = newSpareTime;
    }
}
