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

    private final ScheduleRepository scheduleRepository;
    private final PushNotificationService pushNotificationService;

    public NotificationService(ScheduleRepository scheduleRepository, PushNotificationService pushNotificationService) {
        this.scheduleRepository = scheduleRepository;
        this.pushNotificationService = pushNotificationService;
    }

    public void sendPreScheduleNotifications() {
        System.out.println("서비스계층 시작");
        // 내일 날짜의 시작과 끝 설정
        LocalDateTime startOfDay = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfDay = startOfDay.with(LocalTime.MAX);
        System.out.println("시작일, 종료일:"+startOfDay+endOfDay);

        // 내일 약속이 있는 스케줄 조회
        List<Schedule> schedulesForTomorrow = scheduleRepository.findSchedulesForTomorrow(startOfDay, endOfDay);
        System.out.println("내일 약속인 스케줄 목록:"+schedulesForTomorrow);

        // 알림 전송
        for (Schedule schedule : schedulesForTomorrow) {
            String message = "내일 " + schedule.getScheduleName() + " 약속이 있습니다. 미리 준비하세요!";
            System.out.println(schedule + "약속에 대해 알림 전송 직전");
            pushNotificationService.sendNotification(schedule.getUser(), message);
        }
    }
}