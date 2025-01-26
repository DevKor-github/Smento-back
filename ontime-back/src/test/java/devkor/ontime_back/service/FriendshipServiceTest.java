package devkor.ontime_back.service;

import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoder;

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
        String returnedLink = friendshipService.createFriendshipLink(addedUser.getId());

        // then
        assertThat(returnedLink).isNotNull();
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
        assertThatThrownBy(() -> friendshipService.createFriendshipLink(addedUser.getId() + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }

}