package devkor.ontime_back.controller;

import devkor.ontime_back.ControllerTestSupport;
import devkor.ontime_back.TestSecurityConfig;
import devkor.ontime_back.dto.FinishPreparationDto;
import devkor.ontime_back.dto.LatenessHistoryResponse;
import devkor.ontime_back.dto.UpdateSpareTimeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
class ScheduleControllerTest extends ControllerTestSupport {

    @DisplayName("지각 히스토리 조회에 성공한다.")
    @Test
    void getLatenessHistory() throws Exception {
        // given
        List<LatenessHistoryResponse> latenessHistory = new ArrayList<>();
        latenessHistory.add(new LatenessHistoryResponse(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"),
                "을사년 새해",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                1
        ));

        when(userAuthService.getUserIdFromToken(any())).thenReturn(1L);
        when(scheduleService.getLatenessHistory(any(Long.class))).thenReturn(latenessHistory);

        // when // then
        mockMvc.perform(
                        get("/schedule/lateness-history")
                                .content("")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("지각 히스토리 조회 성공!"))
                .andExpect(jsonPath("$.data[0].scheduleId").value("3fa85f64-5717-4562-b3fc-2c963f66afe5"));
    }

    @DisplayName("약속 종료(성실도점수/지각시간 업데이트)에 성공한다.")
    @Test
    void finishSchedule() throws Exception {
        // given
        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .build();

        when(userAuthService.getUserIdFromToken(any())).thenReturn(1L);
        doNothing().when(scheduleService).finishSchedule(any(Long.class), any(FinishPreparationDto.class));

        // when // then
        mockMvc.perform(
                        put("/schedule/finish")
                                .content(objectMapper.writeValueAsString(finishPreparationDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("지각시간과 성실도점수가 성공적으로 업데이트 되었습니다!"));
    }
}