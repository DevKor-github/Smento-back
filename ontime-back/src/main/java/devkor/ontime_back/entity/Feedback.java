package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private UUID feedbackId;

    // 명시적으로 TEXT 타입으로 정의
    private String message;

    private LocalDateTime createAt; // 약속시각

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
