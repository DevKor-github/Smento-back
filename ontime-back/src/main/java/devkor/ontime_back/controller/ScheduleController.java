package devkor.ontime_back.controller;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 전체 약속 조회
    @Operation(summary = "Get all schedules", description = "Fetch all schedules based on some filters")
    @GetMapping("/show/all")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showAllSchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 오늘의 약속 조회
    @GetMapping("/show/today")
    public ResponseEntity<List<ScheduleDto>> getTodaySchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showTodaySchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 이달의 약속 조회
    @GetMapping("/show/month")
    public ResponseEntity<List<ScheduleDto>> getMonthSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showMonthSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

    // 약속 삭제
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.deleteSchedule(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }

    // 약속 수정
    @PutMapping("/modify")
    public ResponseEntity<Void> modifySchedule(HttpServletRequest request, @RequestBody ScheduleModDto scheduleModDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.modifySchedule(userId, scheduleModDto);
        return ResponseEntity.ok().build();
    }

    // 약속 추가

    @PostMapping("/add")
    public ResponseEntity<Void> addSchedule(HttpServletRequest request, @RequestBody ScheduleAddDto scheduleAddDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.addSchedule(scheduleAddDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 시작 버튼 누름
    @PatchMapping("/start/{scheduleId}")
    public ResponseEntity<Void> isStartedSchedule(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.checkIsStarted(scheduleId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "지각 히스토리 조회 (지각시간이 0초과인 약속들 조회)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "지각 히스토리 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 넣으면 됨.",
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "지각 히스토리 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "[\n  {\n    \"scheduleId\": \"a304cde3-8ee9-4054-971a-300aacc2189b\",\n    \"scheduleName\": \"정보대 해커톤\",\n    \"scheduleTime\": \"2024-11-15T17:05:00\",\n    \"latenessTime\": 3\n  },\n  {\n    \"scheduleId\": \"b784cde3-9ff9-4054-872a-500bbcc2198c\",\n    \"scheduleName\": \"Ontime 회의\",\n    \"scheduleTime\": \"2024-11-16T10:00:00\",\n    \"latenessTime\": 5\n  }\n]"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "지각 히스토리 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/lateness-history") // 지각 히스토리 조회
    public ResponseEntity<List<LatenessHistoryResponse>> getPunctualityPage(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);
        List<LatenessHistoryResponse> latenessHistory = scheduleService.getLatenessHistory(userId);

        return ResponseEntity.ok(latenessHistory);
    }


}
