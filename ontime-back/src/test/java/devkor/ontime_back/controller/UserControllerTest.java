package devkor.ontime_back.controller;

import devkor.ontime_back.dto.UpdateSpareTimeDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.ScheduleService;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 테스트용 애플리케이션 컨텍스트 생성
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mock으로 UserService 주입

    @MockBean
    private UserAuthService userAuthService; // Mock으로 UserAuthService 주입

    @MockBean
    private ScheduleService scheduleService; // Mock으로 ScheduleService 주입

    // 공통 변수
    private static String accessToken;
    private static Long userId;

    @BeforeAll
    public static void setup(@Autowired UserRepository userRepository, @Autowired JwtTokenProvider jwtTokenProvider) {
        // Given: User 데이터 하드 생성
        User user = User.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .spareTime(15) // 여유시간 설정
                .punctualityScore(-1.0f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .role(Role.USER)
                .build();

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
    public void testGetPunctualityScore() throws Exception {
        // Given
        float mockScore = -1.0f;
        Mockito.when(userAuthService.getUserIdFromToken(Mockito.any(HttpServletRequest.class)))
                .thenReturn(userId);
        Mockito.when(userService.getPunctualityScore(userId))
                .thenReturn(mockScore);

        // When & Then
        mockMvc.perform(get("/user/punctuality-score")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.punctualityScore").value(mockScore))
                .andExpect(jsonPath("$.message").value("-1이면 성실도 점수 초기화 직후의 상태. 0~100의 float면 성실도 점수"));
    }

    @Test
    public void testResetPunctualityScore() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(put("/user/reset-punctuality")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("성실도 점수가 성공적으로 초기화 되었습니다! (초기화 이후 약속 수 <- 0, 초기화 이후 지각 수 <- 0, 성실도 점수 <- -1)"));
    }

    @Test
    public void testUpdateSpareTime() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newSpareTime\":30}")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("사용자 여유시간이 성공적으로 업데이트되었습니다!"));
    }
}