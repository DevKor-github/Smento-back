package devkor.ontime_back.service;

import devkor.ontime_back.entity.FriendShip;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FriendshipService {

    @Transactional
    public String createFriendshipLink(Long requesterId) {
        UUID friendshipId = UUID.randomUUID();
        FriendShip friendShip = FriendShip.builder()
                .friendShipId(friendshipId)
                .requesterId(requesterId)
                .status("PENDING")
                .build();

        String friendshipLinkURL = "http://ontime.com/friendship/" + friendshipId;
        return friendshipLinkURL;
    }
}
