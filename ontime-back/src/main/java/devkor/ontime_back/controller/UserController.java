package devkor.ontime_back.controller;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.response.ApiResponseForm;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            @ApiResponse(responseCode = "200", description = "성실도 점수 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"status\": \"success\",\n  \"code\": \"200\",\n  \"message\": \"-1이면 성실도점수 초기화 직후의 상태. 0~100의 float 자료형이면 성실도 점수\",\n  \"data\": {\n    \"punctualityScore\": -1\n  }}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "성실도 점수 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/punctuality-score") // 성실도 점수 조회
    public ResponseEntity<ApiResponseForm<PunctualityScoreResponse>> getPunctualityPage(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);
        float punctualityScore = userService.getPunctualityScore(userId); // -1 or float 0~100 반환

        String message = "-1이면 성실도 점수 초기화 직후의 상태. 0~100의 float면 성실도 점수";
        return ResponseEntity.ok(ApiResponseForm.success(new PunctualityScoreResponse(punctualityScore), message));
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
            @ApiResponse(responseCode = "200", description = "성실도 점수 초기회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"status\": \"success\",\n  \"code\": \"200\",\n  \"message\": \"성실도 점수가 성공적으로 초기화 되었습니다! (초기화 이후 약속 수 <- 0, 초기화 이후 지각 수 <- 0, 성실도 점수 <- -1)\",\n  \"data\": null\n}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "성실도 점수 초기화 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/reset-punctuality") // 성실도 점수 초기화
    public ResponseEntity<ApiResponseForm<String>> resetPunctualityScore(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userService.resetPunctualityScore(userId);

        String message = "성실도 점수가 성공적으로 초기화 되었습니다! (초기화 이후 약속 수 <- 0, 초기화 이후 지각 수 <- 0, 성실도 점수 <- -1)";
        return ResponseEntity.ok(ApiResponseForm.success(null, message));
    }

}

