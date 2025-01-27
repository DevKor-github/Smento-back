package devkor.ontime_back.service;

import devkor.ontime_back.entity.FriendShip;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.FriendshipRepository;
import devkor.ontime_back.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FriendshipServiceTest {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        friendshipRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("친구추가 링크 생성에 성공한다")
    @Test
    void createFriendshipLink(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        // when
        String returnedLink = friendshipService.createFriendShipLink(addedUser.getId());

        // then
        assertThat(returnedLink).isNotNull();
        assertThat(returnedLink).contains("http://ontime.com/friendship/");
        assertThat(friendshipRepository.findByFriendShipId(UUID.fromString(returnedLink.substring(29)))).isNotNull();
    }

    @DisplayName("친구추가 링크 생성시 잘못된 유저가 전달되는 경우 에외가 발생한다")
    @Test
    void createFriendshipLinkWithWrongUser(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        // when // then
        assertThatThrownBy(() -> friendshipService.createFriendShipLink(addedUser.getId() + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }


    @DisplayName("친구추가 요청자 정보 조회에 성공한다." +
            "(Friendship 데이터의 receiverId를 세팅하고 해당 데이터의 수신자 User 정보를 반환에 성공한다.)")
    @Test
    void getFriendRequester(){
        // given
        User addedRequester = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        User addedReceiver = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("jinseo")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedRequester);
        userRepository.save(addedReceiver);

        FriendShip friendShip = FriendShip.builder()
                .friendShipId(UUID.randomUUID())
                .requesterId(addedRequester.getId())
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        // when
        User returnedRequester = friendshipService.getFriendRequester(addedReceiver.getId(), friendShip.getFriendShipId());

        // then
        // 1. 수신자 ID 세팅 확인 2. 요청자 정보 반환 확인
        FriendShip updatedFriendship = friendshipRepository.findByFriendShipId(friendShip.getFriendShipId()).orElseThrow();
        assertThat(updatedFriendship.getReceiverId()).isEqualTo(addedReceiver.getId());

        assertThat(returnedRequester).isNotNull();
        assertThat(returnedRequester.getId()).isEqualTo(addedRequester.getId());
    }

    @DisplayName("친구추가 요청자 정보 조회 시 잘못된 유저id가 전달될 때 예외가 발생한다.")
    @Test
    void getFriendRequesterWithWrongUserId(){
        // given
        User addedRequester = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        User addedReceiver = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("jinseo")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedRequester);
        userRepository.save(addedReceiver);

        FriendShip friendShip = FriendShip.builder()
                .friendShipId(UUID.randomUUID())
                .requesterId(addedRequester.getId())
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        // when // then
        assertThatThrownBy(() -> friendshipService.getFriendRequester(addedReceiver.getId() + 123456789, friendShip.getFriendShipId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }

    @DisplayName("친구추가 요청자 정보 조회 시 잘못된 친구관계id(UUID)가 전달될 때 예외가 발생한다.")
    @Test
    void getFriendRequesterWithWrongFriendShipId(){
        // given
        User addedRequester = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        User addedReceiver = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("jinseo")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedRequester);
        userRepository.save(addedReceiver);

        FriendShip friendShip = FriendShip.builder()
                .friendShipId(UUID.randomUUID())
                .requesterId(addedRequester.getId())
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        // when // then
        assertThatThrownBy(() -> friendshipService.getFriendRequester(addedReceiver.getId(), UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 친구 요청입니다.");
    }

    @DisplayName("친구추가 요청자 정보 조회 시 잘못된 친구추가 요청자id가 전달될 때 예외가 발생한다.")
    @Test
    void getFriendRequesterWithWrongRequeseterId(){
        // given
        User addedRequester = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        User addedReceiver = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("jinseo")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedRequester);
        userRepository.save(addedReceiver);

        FriendShip friendShip = FriendShip.builder()
                .friendShipId(UUID.randomUUID())
                .requesterId(addedRequester.getId())
                .status("PENDING")
                .build();
        friendshipRepository.save(friendShip);

        // 친구추가 요청자 삭제(친구추가 요청한 사람이 Ontime 탈퇴)
        userRepository.delete(addedRequester);

        // when // then
        assertThatThrownBy(() -> friendshipService.getFriendRequester(addedReceiver.getId(), friendShip.getFriendShipId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 친구추가 요청자 id입니다. 해당 유저가 탈퇴했을 수 있습니다.");
    }

}