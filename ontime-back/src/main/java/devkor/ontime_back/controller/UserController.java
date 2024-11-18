package devkor.ontime_back.controller;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.service.ScheduleService;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final ScheduleService scheduleService;

    @Operation(
            summary = "성실도 점수 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "성실도 점수 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "성실도 점수 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"punctuality\": 87.4\n}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "성실도 점수 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/punctuality-score") // 성실도 점수 조회
    public ResponseEntity<Float> getPunctualityPage(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);
        float punctualityScore = userService.getPunctualityScore(userId); // -1 or float 0~100 반환

        return ResponseEntity.ok(punctualityScore);
    }


    @Operation(
            summary = "성실도 점수 초기화",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "성실도 점수 초기화 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "성실도 점수 초기회 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "성실도 점수가 초기화 되었습니다!"))),
            @ApiResponse(responseCode = "4XX", description = "성실도 점수 초기화 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/reset-punctuality") // 성실도 점수 초기화
    public ResponseEntity<String> resetPunctualityScore(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        return ResponseEntity.ok("성실도 점수가 초기화 되었습니다!");
    }

    @Operation(
            summary = "약속 준비 종료 이후 지각시간, 성실도점수 업데이트 (약속 준비 종료 이후 호출해야함)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "성실도 점수 초기화 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"scheduleId\": \"a304cde3-8ee9-4054-971a-300aacc2189a\", \"latenessTime\": 3}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "지각시간, 성실도점수 업데이트 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "해당 약속의 지각시간과 해당 유저의 성실도점수가 성공적으로 업데이트 되었습니다!"))),
            @ApiResponse(responseCode = "4XX", description = "지각시간, 성실도점수 업데이트 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/finish-preparation") // 약속 준비 종료 이후 지각시간(Schedule 테이블), 성실도 점수(User 테이블) 업데이트
    public ResponseEntity<String> finishPreparation(
            HttpServletRequest request,
            @RequestBody FinishPreparationDto finishPreparationDto) {

        Long userId = userAuthService.getUserIdFromToken(request);

        scheduleService.updateLatenessTime(finishPreparationDto);
        userService.updatePunctualityScore(userId, finishPreparationDto.getLatenessTime());

        return ResponseEntity.ok("해당 약속의 지각시간과 해당 유저의 성실도점수가 성공적으로 업데이트 되었습니다!");
    }
}

