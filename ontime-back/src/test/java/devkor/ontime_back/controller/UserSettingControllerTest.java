package devkor.ontime_back.controller;

import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserSettingService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService userAuthService;

    @MockBean
    private UserSettingService userSettingService;

    private static String accessToken;
    private static Long userId;

    @BeforeAll
    public static void setup(@Autowired UserRepository userRepository, @Autowired JwtTokenProvider jwtTokenProvider, @Autowired PasswordEncoder passwordEncoder) {
        // Given: User 데이터 생성
        User user = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("Test User")
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        userId = user.getId();

        // JWT 토큰 생성
        accessToken = jwtTokenProvider.createAccessToken("test@example.com", userId);
    }

    @AfterAll
    public static void cleanup(@Autowired UserRepository userRepository) {
        // 테스트 데이터 삭제
        userRepository.deleteAll();
    }

    @Test
    public void testUpdateSetting() throws Exception {
        // Given
        String updateSettingJson = "{ \"isNotificationsEnabled\": true, \"soundVolume\": 75, \"isPlayOnSpeaker\": false, \"is24HourFormat\": true }";
        Mockito.doNothing().when(userSettingService).updateSetting(Mockito.eq(userId), Mockito.any());

        // When & Then
        mockMvc.perform(put("/user-setting/update")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSettingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("사용자 앱 설정이 성공적으로 업데이트되었습니다!"));
    }

    @Test
    public void testResetSetting() throws Exception {
        // Given
        Mockito.doNothing().when(userSettingService).resetSetting(Mockito.eq(userId));

        // When & Then
        mockMvc.perform(put("/user-setting/reset")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("사용자 앱 설정이 성공적으로 초기화되었습니다! (soundVolume 50, 나머지 모두 true)"));
    }
}