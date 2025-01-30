package devkor.ontime_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@RequiredArgsConstructor
public class FriendShip {

    @Id
    private UUID friendShipId;

    @Column(nullable = false)
    private Long requesterId;

    @Column
    private Long receiverId;

    private String acceptStatus; // "PENDING", "ACCEPTED", "REJECTED"

    @Builder
    public FriendShip(UUID friendShipId, Long requesterId, Long receiverId, String acceptStatus) {
        this.friendShipId = friendShipId;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.acceptStatus = acceptStatus;
    }

    public void setReceiverId(Long recieverId) {
        this.receiverId = recieverId;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }
}
