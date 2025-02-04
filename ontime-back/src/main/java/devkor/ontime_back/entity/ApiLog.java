package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
public class ApiLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ApiLogId;

    private String requestUrl;

    private String requestMethod;

    private String userId;

    private String clientIp;

    private int responseStatus;

    private long takenTime;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ApiLog(String requestUrl, String requestMethod, String userId, String clientIp, int responseStatus, long takenTime) {
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        this.userId = userId;
        this.clientIp = clientIp;
        this.responseStatus = responseStatus;
        this.takenTime = takenTime;
    }
}
