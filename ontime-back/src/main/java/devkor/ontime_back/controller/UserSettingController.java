package devkor.ontime_back.controller;

import devkor.ontime_back.dto.ScheduleModDto;
import devkor.ontime_back.dto.UserSettingUpdateDto;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserSettingService;
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
@RequestMapping("/user-setting")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserAuthService userAuthService;
    private final UserSettingService userSettingService;

    @Operation(
            summary = "사용자 앱 설정 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 앱 설정 업데이트 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"isNotificationsEnabled\": \"true\", \"soundVolume\": 75, \"isPlayOnSpeaker\": \"false\", \"is24HourFormat\": \"true\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 앱 설정 업데이트 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = "사용자 앱 설정 업데이트 완료")
            )),
            @ApiResponse(responseCode = "4XX", description = "사용자 앱 설정 업데이트 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/update")
    public ResponseEntity<String> updateSetting(HttpServletRequest request, @RequestBody UserSettingUpdateDto userSettingUpdateDto) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userSettingService.updateSetting(userId, userSettingUpdateDto);

        return ResponseEntity.ok("사용자 앱 설정 업데이트 완료");
    }

    @Operation(
            summary = "사용자 앱 설정 초기화",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 앱 설정 초기화 요청 JSON 데이터는 없음. 헤더에 토큰만 보내면 됨.",
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
            @ApiResponse(responseCode = "200", description = "사용자 앱 설정 초기화 완료", content = @Content(mediaType = "application/json", schema = @Schema(example = "사용자 앱 설정 초기화 완료")
            )),
            @ApiResponse(responseCode = "4XX", description = "사용자 앱 설정 초기화 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/reset")
    public ResponseEntity<String> resetSetting(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userSettingService.resetSetting(userId);

        return ResponseEntity.ok("사용자 앱 설정 초기화 완료");
    }
}
