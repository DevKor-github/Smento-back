package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;

@Getter
@Entity
public class PreparationUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preparationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Time preparationTime;

    private Long sortNum;
}
