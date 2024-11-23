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
            @ApiResponse(responseCode = "200", description = "성실도 점수 조회 성공", content = @Content(
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
            @ApiResponse(responseCode = "200", description = "성실도 점수 초기회 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "성실도 점수가 초기화 되었습니다!"))),
            @ApiResponse(responseCode = "4XX", description = "성실도 점수 초기화 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/reset-punctuality") // 성실도 점수 초기화
    public ResponseEntity<String> resetPunctualityScore(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        return ResponseEntity.ok("성실도 점수가 초기화 되었습니다!");
    }

}

