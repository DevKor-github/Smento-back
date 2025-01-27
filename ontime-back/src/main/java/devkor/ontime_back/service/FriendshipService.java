package devkor.ontime_back.service;

import devkor.ontime_back.entity.FriendShip;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.FriendshipRepository;
import devkor.ontime_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createFriendShipLink(Long requesterId) {

        userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 id입니다."));

        UUID friendShipId = UUID.randomUUID();
        FriendShip friendShip = FriendShip.builder()
                .friendShipId(friendShipId)
                .requesterId(requesterId)
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        return "http://ontime.com/friendship/" + friendShipId;
    }


    @Transactional
    public User getFriendRequester(Long recieverId, UUID friendshipId) {

        userRepository.findById(recieverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 id입니다."));

        FriendShip friendShip = friendshipRepository.findByFriendShipId(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));


        // UUID로 조회한 FriendShip 데이터에 수신자 ID 세팅
        friendShip.setReceiverId(recieverId);
        friendshipRepository.save(friendShip);

        // UUID로 조회한 FriendShip 데이터의 요청자 조회 및 반환
        return userRepository.findById(friendShip.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구추가 요청자 id입니다. 해당 유저가 탈퇴했을 수 있습니다."));
    }
}
