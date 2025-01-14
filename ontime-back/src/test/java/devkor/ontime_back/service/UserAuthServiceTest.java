package devkor.ontime_back.service;

import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.entity.UserSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAuthServiceTest {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserSettingService userSettingService;

    @DisplayName("회원정보를 받아 User테이블과 UserSetting테이블에 데이터를 생성하고 연관관계를 생성한다.(회원가입을 한다.)")
    @Test
    void signUp() throws Exception {
        // given
        UserSignUpDto userSignUpDto = UserSignUpDto.builder()
                .email("user@example.com")
                .password("password1234")
                .name("junbeom")
                .userSettingId(UUID.fromString("a304cde3-8ee9-4054-971a-300aacc2177c"))
                .build();

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
}