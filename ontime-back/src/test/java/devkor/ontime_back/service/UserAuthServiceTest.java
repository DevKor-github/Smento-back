package devkor.ontime_back.service;

import devkor.ontime_back.dto.ChangePasswordDto;
import devkor.ontime_back.dto.UserAdditionalInfoDto;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.entity.UserSetting;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.repository.UserSettingRepository;
import devkor.ontime_back.response.GeneralException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.NoSuchElementException;
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
    private ServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;

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
        User addedUser = userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse, userSignUpDto);
        UserSetting userSetting = addedUser.getUserSetting();

        // then
        assertThat(addedUser.getId()).isNotNull();
        assertThat(addedUser)
                .extracting("email", "name", "punctualityScore", "scheduleCountAfterReset", "latenessCountAfterReset")
                .contains("user@example.com", "junbeom", -1f, 0, 0);
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
        User addedUser1 = userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto2))
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
        User addedUser1 = userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto2))
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
        User addedUser1 = userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto1);
        assertThat(addedUser1.getId()).isNotNull();

        assertThatThrownBy(() -> userAuthService.signUp((HttpServletRequest) httpServletRequest, httpServletResponse,userSignUpDto2))
                .isInstanceOf(GeneralException.class)
                .hasMessage("이미 존재하는 userSettingId 입니다.");
    }

    @DisplayName("(회원가입 직후) 유저의 추가정보 기입에 성공한다")
    @Test
    void addInfo() throws Exception {
        // given(유저데이터 하드저장 및 추가 기입 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password("password1234")
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        UserAdditionalInfoDto userAdditionalInfoDto = UserAdditionalInfoDto.builder()
                .spareTime(10)
                .note("내 인생에 이제 지각은 없다!!!")
                .build();

        // when(추가정보 기입)
        User additionalInfoAddedUser = userAuthService.addInfo(addedUser.getId(), userAdditionalInfoDto);

        // then
        assertThat(additionalInfoAddedUser.getId()).isNotNull();
        assertThat(additionalInfoAddedUser.getId()).isEqualTo(addedUser.getId());
        assertThat(additionalInfoAddedUser)
                .extracting("spareTime", "note")
                .contains(10, "내 인생에 이제 지각은 없다!!!");
    }

    @DisplayName("존재하지 않는 유저의 추가정보를 기입하는 경우 예외가 발생한다")
    @Test
    void addInfoWithWrongUser() throws Exception {
        // given(유저데이터 하드저장 및 추가 기입 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password("password1234")
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        UserAdditionalInfoDto userAdditionalInfoDto = UserAdditionalInfoDto.builder()
                .spareTime(10)
                .note("내 인생에 이제 지각은 없다!!!")
                .build();

        // when // then
        assertThatThrownBy(() -> userAuthService.addInfo(2L, userAdditionalInfoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }

    @DisplayName("비밀번호 변경에 성공한다")
    @Test
    void changePassword(){
        // given(유저 데이터 하드저장 및 비밀번호 변경 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password1234")
                .newPassword("password12345")
                .build();

        // when
        User passwordChangedUser = userAuthService.changePassword(addedUser.getId(), changePasswordDto);

        // then
        assertThat(passwordChangedUser.getId()).isNotNull();
        assertThat(passwordChangedUser.getId()).isEqualTo(addedUser.getId());
        assertThat(passwordEncoder.matches("password12345", passwordChangedUser.getPassword())).isTrue();
     }

    @DisplayName("비밀번호 변경 시 존재하지 않는 유저id를 인자로 넘기는 경우 예외가 발생한다.")
    @Test
    void changePasswordWithWrongUserId(){
        // given(유저 데이터 하드저장 및 비밀번호 변경 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password1234")
                .newPassword("password12345")
                .build();

        // when // then
        assertThatThrownBy(() -> userAuthService.changePassword(2L, changePasswordDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }

    @DisplayName("비밀번호 변경시 현재 비밀번호를 잘못 입력한 경우 예외가 발생한다.")
    @Test
    void changePasswordWithWrongCurrentPassword(){
        // given(유저 데이터 하드저장 및 비밀번호 변경 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password123")
                .newPassword("password12345")
                .build();

        // when // then
        assertThatThrownBy(() -> userAuthService.changePassword(addedUser.getId(), changePasswordDto))
                .isInstanceOf(GeneralException.class)
                .hasMessage("기존 비밀번호가 틀렸습니다.");
    }

    @DisplayName("비밀번호 변경시 새 비밀번호를 현재 비밀번호와 같게 입력한 경우 예외가 발생한다.")
    @Test
    void changePasswordWithSamePassword(){
        // given(유저 데이터 하드저장 및 비밀번호 변경 DTO 생성)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password1234")
                .newPassword("password1234")
                .build();

        // when // then
        assertThatThrownBy(() -> userAuthService.changePassword(addedUser.getId(), changePasswordDto))
                .isInstanceOf(GeneralException.class)
                .hasMessage("새 비밀번호와 기존 비밀번호가 일치합니다.");
    }

    @DisplayName("계정 삭제에 성공한다")
    @Test
    void deleteUser(){
        // given(유저 데이터 하드저장 및 검증을 위해 해당 유저 아이디 tagerUserId에 저장)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .build();
        userRepository.save(addedUser);

        Long targetUserId = addedUser.getId();

        // when
        Long deletedUserId = userAuthService.deleteUser(addedUser.getId());

        // then
        assertThat(deletedUserId).isNotNull();
        assertThat(deletedUserId).isEqualTo(targetUserId);
        assertThat(userRepository.findById(targetUserId)).isEmpty();
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