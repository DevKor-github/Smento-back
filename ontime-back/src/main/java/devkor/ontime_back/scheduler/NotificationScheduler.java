package devkor.ontime_back.scheduler;

import devkor.ontime_back.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {
    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    //개발 테스트용 (10초마다 실행)
    // 매일 밤 9시에 실행되는 애너테이션 : @Scheduled(cron = "0 0 21 * * *")
    @Scheduled(fixedRate = 10000)
    public void sendPreScheduleNotifications() {
        System.out.println("스케줄러");
        notificationService.sendPreScheduleNotifications();
    }
}
