package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class UserSettingUpdateDto {
    private Boolean isNotificationsEnabled;
    private Integer soundVolume;
    private Boolean isPlayOnSpeaker;
    private Boolean is24HourFormat;
}
