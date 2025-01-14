package devkor.ontime_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class FriendShip {

    @Id
    private UUID friendShipId;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long recieverId;

    private String status;
}
