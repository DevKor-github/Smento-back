package devkor.ontime_back.service;

import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.ScheduleRepository;
import devkor.ontime_back.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {

    public void sendReminder(List<Schedule> schedules, String message) {
        for (Schedule schedule : schedules) {
            sendNotificationToUser(schedule, message);
        }
    }

    private void sendNotificationToUser(Schedule schedule, String message) {
        // 사용자 디바이스로 알림을 보내는 로직으로 변환 해야함. (예: FCM 사용)
        User user = schedule.getUser();
        System.out.println(user.getName() + "님 " + message + "\n약속: " + schedule);
    }
}