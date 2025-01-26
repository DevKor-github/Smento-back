package devkor.ontime_back.service;

import devkor.ontime_back.entity.FriendShip;
import devkor.ontime_back.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    @Transactional
    public String createFriendshipLink(Long requesterId) {
        UUID friendshipId = UUID.randomUUID();
        FriendShip friendShip = FriendShip.builder()
                .friendShipId(friendshipId)
                .requesterId(requesterId)
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        return "http://ontime.com/friendship/" + friendshipId;
    }
}
