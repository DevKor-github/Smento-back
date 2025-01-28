package devkor.ontime_back.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserOnboardingDto {
    private Integer spareTime;
    private String note;
    private List<PreparationDto> preparationList;
}
