package devkor.ontime_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.dto.ChangePasswordDto;
import devkor.ontime_back.dto.UserAdditionalInfoDto;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.UserAuthService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService userAuthService;

    // 공통 변수
    private static String accessToken;
    private static Long userId;

    @BeforeAll
    public static void setup(@Autowired UserRepository userRepository, @Autowired JwtTokenProvider jwtTokenProvider, @Autowired PasswordEncoder passwordEncoder) {
        // Given: User 데이터 하드 생성. 여유시간은 초기화하지 않음(for AddInfo 테스트)
        User user = User.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .punctualityScore(-1.0f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .role(Role.USER)
                .build();
        user.passwordEncode(passwordEncoder);

        user = userRepository.save(user);
        userId = user.getId();

        // 엑세스 토큰 hard하게 생성
        accessToken = jwtTokenProvider.createAccessToken("test@example.com", userId);
    }

    @AfterAll
    public static void cleanup(@Autowired UserRepository userRepository) {
        // 테스트 데이터 정리
        userRepository.deleteAll();
    }


    @Test
    public void testLoginSuccess() throws Exception {
        // Given: 로그인 요청 JSON 데이터
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password123\" }";

        // When & Then: 로그인 성공 시 응답 검증
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization")); // JWT 토큰이 반환되었는지 확인
    }


    @Test
    public void testLoginFailure() throws Exception {
        // Given: 잘못된 로그인 요청 JSON 데이터
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"wrongPassword\" }";

        // When & Then: 로그인 실패 시 응답 검증
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").value("로그인 실패! 이메일이나 비밀번호를 확인해주세요."));
    }

    @Test
    public void testAddInfo() throws Exception {
        // Given
        UserAdditionalInfoDto additionalInfoDto = new UserAdditionalInfoDto(15, "약속을 지키는 사람이 되자!");

        // When & Then
        mockMvc.perform(put("/" + userId + "/additional-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(additionalInfoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("추가 정보가 성공적으로 기입되었습니다!"));
    }

    @Test
    public void testChangePassword() throws Exception {
        // Given
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("password123", "1q2w3e4r!");

        // When & Then
        mockMvc.perform(put("/change-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다!"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/user/delete")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("계정이 성공적으로 삭제되었습니다!"));
    }

}