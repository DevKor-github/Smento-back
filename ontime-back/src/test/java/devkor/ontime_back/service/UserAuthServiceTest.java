package devkor.ontime_back.service;

import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.entity.UserSetting;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.repository.UserSettingRepository;
import devkor.ontime_back.response.GeneralException;
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
class UserAuthServiceTest {

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userSettingRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원정보를 받아 User테이블과 UserSetting테이블에 데이터를 생성하고 연관관계를 생성한다.(회원가입을 한다.)")
    @Test
    void signUp() throws Exception {
        // given
        UserSignUpDto userSignUpDto = getUserSignUpDto("user@example.com", "password1234", "junbeom", "a304cde3-8ee9-4054-971a-300aacc2177c");

        // when
        User addedUser = userAuthService.signUp(userSignUpDto);
        UserSetting userSetting = addedUser.getUserSetting();

        // then
        assertThat(addedUser.getId()).isNotNull();
        assertThat(addedUser)
                .extracting("email", "name")
                .contains("user@example.com", "junbeom");
        assertThat(passwordEncoder.matches("password1234", addedUser.getPassword())).isTrue();
        assertThat(userSetting).isNotNull();
        assertThat(userSetting.getUserSettingId())
                .isEqualTo(UUID.fromString("a304cde3-8ee9-4054-971a-300aacc2177c"));

    }

    @DisplayName("이미 존재하는 이메일로 회원가입을 시도하는 경우 예외가 발생한다.")
    @Test
    void signUpWithExistingEmail() throws Exception {
        // given
        UserSignUpDto userSignUpDto1 = getUserSignUpDto("user@example.com", "password1234", "junbeom", "a304cde3-8ee9-4054-971a-300aacc2177c");

        UserSignUpDto userSignUpDto2 = getUserSignUpDto("user@example.com", "password1234", "junbeom2", "a304cde3-8ee9-4054-971a-300aacc2177d");

        // when, then
        User addedUser1 = userAuthService.signUp(userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp(userSignUpDto2))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @DisplayName("이미 존재하는 이름으로 회원가입을 시도하는 경우 예외가 발생한다.")
    @Test
    void signUpWithExistingName() throws Exception {
        // given
        UserSignUpDto userSignUpDto1 = getUserSignUpDto("user@example.com", "password1234", "junbeom", "a304cde3-8ee9-4054-971a-300aacc2177c");

        UserSignUpDto userSignUpDto2 = getUserSignUpDto("user2@example.com", "password1234", "junbeom", "a304cde3-8ee9-4054-971a-300aacc2177d");

        // when, then
        User addedUser1 = userAuthService.signUp(userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp(userSignUpDto2))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이미 존재하는 이름입니다.");
    }

    @DisplayName("이미 존재하는 유저세팅ID로 회원가입을 시도하는 경우 예외가 발생한다.")
    @Test
    void signUpWithExistingUserSettingId() throws Exception {
        // given
        UserSignUpDto userSignUpDto1 = getUserSignUpDto("user@example.com", "password1234", "junbeom", "a304cde3-8ee9-4054-971a-300aacc2177c");

        UserSignUpDto userSignUpDto2 = getUserSignUpDto("user2@example.com", "password1234", "junbeom2", "a304cde3-8ee9-4054-971a-300aacc2177c");

        // when, then
        User addedUser1 = userAuthService.signUp(userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp(userSignUpDto2))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이미 존재하는 userSettingId 입니다.");
    }

    private UserSignUpDto getUserSignUpDto(String email, String password, String name, String userSettingId) {
        UserSignUpDto userSignUpDto = UserSignUpDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .userSettingId(UUID.fromString(userSettingId))
                .build();
        return userSignUpDto;
    }
}