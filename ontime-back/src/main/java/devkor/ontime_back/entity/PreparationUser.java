package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;
import java.util.UUID;

@Getter
@Entity
public class PreparationUser {
    @Id
    private UUID preparationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Time preparationTime;

    private Long sortNum;
}
