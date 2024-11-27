package devkor.ontime_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.dto.*;
import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.ScheduleService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

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
        accessToken = jwtTokenProvider.createAccessToken("test@example.com", userId);

        // place, schedule 추가
        Place place = new Place();
        place.initPlaceName(UUID.randomUUID(), "Home");

        LocalDateTime scheduleTime = LocalDateTime.of(2024,11,16,18,0);

        Schedule schedule = Schedule.builder()
                .scheduleId(UUID.fromString("123e4567-e89b-12d3-a456-426614170105"))
                .user(user)
                .place(place)
                .scheduleName("Birthday Party")
                .scheduleTime(scheduleTime)
                .build();
    }

    @AfterAll
    public static void cleanup(@Autowired UserRepository userRepository) {
        // 테스트 데이터 삭제
        userRepository.deleteAll();
    }

    @Test
    public void testGetPeriodSchedule() throws Exception {
        // Given
        String startDate = "2024-11-15T18:00:00";
        String endDate = "2024-11-18T20:00:00";

        // When & Then
        mockMvc.perform(get("/schedule/show")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].scheduleId").value("123e4567-e89b-12d3-a456-426614170105"));
    }


}