package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class Friend {

    @Id
    private UUID friendId;

    private String friendName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
