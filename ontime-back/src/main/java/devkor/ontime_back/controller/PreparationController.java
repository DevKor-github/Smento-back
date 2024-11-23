package devkor.ontime_back.controller;

import devkor.ontime_back.dto.FinishPreparationDto;
import devkor.ontime_back.service.PreparationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preparation")
@RequiredArgsConstructor
public class PreparationController {
    private final UserAuthService userAuthService;
    private final ScheduleService scheduleService;
    private final UserService userService;
    private final PreparationService preparationService;

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
            @ApiResponse(responseCode = "200", description = "지각시간, 성실도점수 업데이트 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "해당 약속의 지각시간과 해당 유저의 성실도점수가 성공적으로 업데이트 되었습니다!"))),
            @ApiResponse(responseCode = "4XX", description = "지각시간, 성실도점수 업데이트 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/finish") // 약속 준비 종료 이후 지각시간(Schedule 테이블), 성실도 점수(User 테이블) 업데이트
    public ResponseEntity<String> finishPreparation(
            HttpServletRequest request,
            @RequestBody FinishPreparationDto finishPreparationDto) {

        Long userId = userAuthService.getUserIdFromToken(request);

        preparationService.finishPreparation(userId, finishPreparationDto);

        return ResponseEntity.ok("해당 약속의 지각시간과 해당 유저의 성실도점수가 성공적으로 업데이트 되었습니다!");
    }
}
