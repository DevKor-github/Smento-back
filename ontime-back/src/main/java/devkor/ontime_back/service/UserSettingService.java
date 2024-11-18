package devkor.ontime_back.service;

import devkor.ontime_back.dto.UserSettingUpdateDto;
import devkor.ontime_back.entity.UserSetting;
import devkor.ontime_back.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserSettingRepository userSettingRepository;

    public void updateSetting(Long userId, UserSettingUpdateDto userSettingUpdateDto) {
        UserSetting userSetting = userSettingRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("UserSetting not found with given userId"));

        // scheduleDto에서 수정
        userSetting.updateUserSetting(
                userSettingUpdateDto.getIsNotificationsEnabled(),
                userSettingUpdateDto.getSoundVolume(),
                userSettingUpdateDto.getIsPlayOnSpeaker(),
                userSettingUpdateDto.getIs24HourFormat());

        userSettingRepository.save(userSetting);
    }
}
