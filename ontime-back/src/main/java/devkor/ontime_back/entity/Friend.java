package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    private String friendName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
