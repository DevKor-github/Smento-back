package devkor.ontime_back.service;

import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.entity.UserSetting;
import devkor.ontime_back.repository.ScheduleRepository;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserSettingRepository userSettingRepository;

    public void sendReminder(List<Schedule> schedules, String message) {
        for (Schedule schedule : schedules) {
            User user = schedule.getUser();
            Long userId = user.getId();

            if (userId != null) {
                // UserSetting 테이블에서 해당 유저의 알림 설정 가져오기
                UserSetting userSetting = userSettingRepository.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No UserSetting found in schedule's user"));// Repository 메서드 가정

                // 알림 설정 확인
                if (userSetting != null && userSetting.getIsNotificationsEnabled()) {
                    sendNotificationToUser(schedule, message);
                }
            }
        }
    }

    private void sendNotificationToUser(Schedule schedule, String message) {
        // 사용자 디바이스로 알림을 보내는 로직으로 변환 해야함. (예: FCM 사용)
        User user = schedule.getUser();
        System.out.println(user.getName() + "님 " + message + "\n약속: " + schedule);
    }
}