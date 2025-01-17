package devkor.ontime_back.controller;

import devkor.ontime_back.ControllerTestSupport;
import devkor.ontime_back.TestSecurityConfig;
import devkor.ontime_back.dto.ChangePasswordDto;
import devkor.ontime_back.dto.UpdateSpareTimeDto;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
class UserControllerTest extends ControllerTestSupport {

    @DisplayName("성실도 점수 조회에 성공한다.")
    @Test
    void getPunctualityScore() throws Exception {
        // given
        when(userAuthService.getUserIdFromToken(any())).thenReturn(1L);
        when(userService.getPunctualityScore(any(Long.class))).thenReturn(99.999f);

        // when // then
        mockMvc.perform(
                        get("/user/punctuality-score")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("-1이면 성실도점수 초기화 직후의 상태. 0~100의 float 자료형이면 성실도 점수"))
                .andExpect(jsonPath("$.data.punctualityScore").value(99.999f));
    }

    @DisplayName("성실도 점수 초기회에 성공한다.")
    @Test
    void resetPunctualityScore() throws Exception {
        // given
        when(userAuthService.getUserIdFromToken(any())).thenReturn(1L);
        when(userService.resetPunctualityScore(any(Long.class))).thenReturn(-1f);

        // when // then
        mockMvc.perform(
                        put("/user/reset-punctuality")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("성실도 점수가 성공적으로 초기화 되었습니다! (초기화 이후 약속 수 <- 0, 초기화 이후 지각 수 <- 0, 성실도 점수 <- -1)"));
    }

    @DisplayName("여유시간 업데이트에 성공한다.")
    @Test
    void updateSpareTime() throws Exception {
        // given
        UpdateSpareTimeDto updateSpareTimeDto = UpdateSpareTimeDto.builder().newSpareTime(10).build();

        when(userAuthService.getUserIdFromToken(any())).thenReturn(1L);
        // 원래 updateSpareTime는 User객체를 반환하지만 컨트롤러에서는 반환값을 사용하지 않으므로 null로 설정하였음.
        when(userService.updateSpareTime(any(Long.class), any(UpdateSpareTimeDto.class))).thenReturn(null);

        // when // then
        mockMvc.perform(
                        put("/user/spare-time")
                                .content(objectMapper.writeValueAsString(updateSpareTimeDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("사용자 여유시간이 성공적으로 업데이트되었습니다!"));
    }

}