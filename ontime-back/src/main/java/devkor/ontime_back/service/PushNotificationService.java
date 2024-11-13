package devkor.ontime_back.service;

import devkor.ontime_back.entity.User;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    public void sendNotification(User user, String message) {
        // FCM 또는 다른 푸시 알림 API 호출
        // 예: FCM 사용 시 FirebaseMessaging API로 사용자 토큰을 통해 알림 전송
        System.out.println("Sending notification to user: " + user.getName() + " - " + message);

        // 실제 구현에서는 Firebase Cloud Messaging (FCM) 또는 다른 푸시 알림 API 사용
        // 이 부분은 예시이므로, 실제 알림 전송 로직을 API와 통합하여 구현해야 합니다.
    }
}
