package devkor.ontime_back.controller;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "사용자의 모든 일정 조회",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "모든 일정 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
                required = true,
                content = @Content(
                    schema = @Schema(
                            type = "object",
                            example = "{}"
                    )
                )
                )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스케줄 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[{\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afe5\", \n \"placeName\": \"Study Room\", \n \"scheduleName\": \"Friends Meeting\",\n \"moveTime\": \"01:20:00\",\n \"scheduleTime\": \"2024-11-16T19:30:00\",\n \"scheduleSpareTime\": \"00:20:00\",\n \"scheduleNote\": \"Prepare present for friend.\",\n \"latenessTime\": 0\n  }, {\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \n \"placeName\": \"Cafe Lounge\", \n \"scheduleName\": \"School Meeting\",\n \"moveTime\": \"00:20:00\",\n \"scheduleTime\": \"2024-11-14T19:30:00\",\n \"scheduleSpareTime\": \"00:15:00\",\n \"scheduleNote\": \"Check project updates and next steps\",\n \"latenessTime\": 0\n  }]"
                            )
                    )),
                    @ApiResponse(responseCode = "4XX", description = "스케줄을 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"No schedules found.\"}")
                            )
                    )
    })
    @GetMapping("/show/all")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showAllSchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 오늘의 약속 조회
    @Operation(summary = "사용자의 오늘 일정 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "오늘 일정 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오늘 스케줄 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[{\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afe5\", \n \"placeName\": \"Home\", \n \"scheduleName\": \"Birthday Party\",\n \"moveTime\": \"00:00:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"scheduleSpareTime\": \"00:20:00\",\n \"scheduleNote\": \"Write a message.\",\n \"latenessTime\": 0\n  }, {\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa1\", \n \"placeName\": \"Cafe\", \n \"scheduleName\": \"Professor Meeting\",\n \"moveTime\": \"00:10:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"scheduleSpareTime\": \"00:15:00\",\n \"scheduleNote\": \"Ready for everything.\",\n \"latenessTime\": 0\n  }]"
                            )
                    )),
            @ApiResponse(responseCode = "4XX", description = "오늘 스케줄을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"No schedules found.\"}")
                    )
            )
    })
    @GetMapping("/show/today")
    public ResponseEntity<List<ScheduleDto>> getTodaySchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showTodaySchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 이달의 약속 조회
    @Operation(summary = "사용자의 이달 일정 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이달 일정 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이달의 스케줄 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[{\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afe5\", \n \"placeName\": \"Home\", \n \"scheduleName\": \"Birthday Party\",\n \"moveTime\": \"00:00:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"scheduleSpareTime\": \"00:20:00\",\n \"scheduleNote\": \"Write a message.\",\n \"latenessTime\": 0\n  }, {\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa1\", \n \"placeName\": \"Cafe\", \n \"scheduleName\": \"Professor Meeting\",\n \"moveTime\": \"00:10:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"scheduleSpareTime\": \"00:15:00\",\n \"scheduleNote\": \"Ready for everything.\",\n \"latenessTime\": 0\n  }]"
                            )
                    )),
            @ApiResponse(responseCode = "4XX", description = "이달의 스케줄을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"No schedules found.\"}")
                    )
            )
    })
    @GetMapping("/show/month")
    public ResponseEntity<List<ScheduleDto>> getMonthSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showMonthSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

    // 약속 삭제
    @Operation(summary = "사용자 일정 삭제",
            parameters = {
                    @Parameter(
                            name = "scheduleId",
                            description = "삭제할 일정의 ID",
                            required = true,
                            example = "3fa85f64-5717-4562-b3fc-2c963f66afe5"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "일정 삭제 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = " "))),
            @ApiResponse(responseCode = "4XX", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.deleteSchedule(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }

    // 약속 수정
    @Operation(
            summary = "사용자 일정 수정",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 수정 일정 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afe5\", \n \"placeId\": \"70d460da-6a82-4c57-a285-567cdeda5670\", \n \"placeName\": \"Home\", \n \"scheduleName\": \"Party\",\n \"moveTime\": \"00:00:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"scheduleSpareTime\": \"00:20:00\",\n \"scheduleNote\": \"Write a message.\",\n \"latenessTime\": 0\n  }"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "일정 수정 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = ""))),
            @ApiResponse(responseCode = "4XX", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/modify")
    public ResponseEntity<Void> modifySchedule(HttpServletRequest request, @RequestBody ScheduleModDto scheduleModDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.modifySchedule(userId, scheduleModDto);
        return ResponseEntity.ok().build();
    }

    // 약속 추가
    @Operation(
            summary = "사용자 일정 추가",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 일정 추가 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\n \"scheduleId\": \"3fa85f64-5717-4562-b3fc-2c963f66afe5\", \n \"placeId\": \"70d460da-6a82-4c57-a285-567cdeda5670\", \n \"placeName\": \"Home\", \n \"scheduleName\": \"Birthday Party\",\n \"moveTime\": \"00:00:00\",\n \"scheduleTime\": \"2024-11-15T19:30:00\",\n \"isChange\": false\n, \n \"isStarted\": false\n, \n \"scheduleSpareTime\": \"00:20:00\",\n \"scheduleNote\": \"Write a message.\"  }"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 추가 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = ""))),
            @ApiResponse(responseCode = "4XX", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PostMapping("/add")
    public ResponseEntity<Void> addSchedule(HttpServletRequest request, @RequestBody ScheduleAddDto scheduleAddDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.addSchedule(scheduleAddDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 시작 버튼 누름
    @Operation(summary = "일정 시작 버튼 누름",
            parameters = {
                    @Parameter(
                            name = "scheduleId",
                            description = "시작할 일정의 ID",
                            required = true,
                            example = "3fa85f64-5717-4562-b3fc-2c963f66afe5"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 추가 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = ""))),
            @ApiResponse(responseCode = "4XX", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
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
            @ApiResponse(responseCode = "200", description = "지각 히스토리 조회 성공", content = @Content(
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

    @Operation(summary = "준비과정 조회",
            parameters = {
                    @Parameter(
                            name = "scheduleId",
                            description = "확인할 일정의 ID",
                            required = true,
                            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "준비과정 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "[\n {\n \"preparationId\": \"123e4567-e89b-12d3-a456-426614174011\",\n \"preparationName\": \"Step 1: Wake up\",\n \"preparationTime\": 5,\n \"nextPreparationId\": \"123e4567-e89b-12d3-a456-426614174012\"\n },\n {\n\"preparationId\": \"123e4567-e89b-12d3-a456-426614174012\",\n\"preparationName\": \"Step 2: Brush teeth\",\n \"preparationTime\": 15,\n \"nextPreparationId\": \"123e4567-e89b-12d3-a456-426614174013\"\n },\n {\n\"preparationId\": \"123e4567-e89b-12d3-a456-426614174013\",\n\"preparationName\": \"Step 3: Wearing Clothes\",\n\"preparationTime\": 15,\n\"nextPreparationId\": \"123e4567-e89b-12d3-a456-426614174014\"\n },\n{\n\"preparationId\": \"123e4567-e89b-12d3-a456-426614174014\",\n\"preparationName\": \"Step 4: Breakfast\",\n\"preparationTime\": 30,\n\"nextPreparationId\": null\n }\n ]"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "준비과정 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/get/preparation/{scheduleId}")
    public ResponseEntity<List<PreparationDto>> getPreparation(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);
        List<PreparationDto> preparationDtoList = scheduleService.getPreparations(userId, scheduleId);

        return ResponseEntity.ok(preparationDtoList);
    }




}
