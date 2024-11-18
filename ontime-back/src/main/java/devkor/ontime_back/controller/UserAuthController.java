package devkor.ontime_back.controller;

import devkor.ontime_back.dto.ChangePasswordDto;
import devkor.ontime_back.dto.ChangePasswordResponse;
import devkor.ontime_back.dto.UserAdditionalInfoDto;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final UserRepository userRepository;

    @Operation(summary = "일반 회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "회원가입 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"message\": \"회원가입 성공\",\n  \"userId\": 1}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "회원가입 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object", example = "{\"email\": \"user@example.com\", \"password\": \"password123\", \"name\": \"junbeom\", \"userSettingId\": \"a304cde3-8ee9-4054-971a-300aacc2177c\"}")
                    )
            )
            @RequestBody UserSignUpDto userSignUpDto) throws Exception {
        Long userId = userAuthService.signUp(userSignUpDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입 성공");
        response.put("userId", userId);

        // ResponseEntity로 반환
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "일반 로그인 (로그인 요청을 통해 JWT 토큰을 발급받음)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "일반 로그인 성공(반환 문자열 없음)", content = @Content(mediaType = "application/json", schema = @Schema(example = " "))),
            @ApiResponse(responseCode = "4XX", description = "일반 로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PostMapping("/login")
    public String login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object", example = "{\"email\": \"user@example.com\", \"password\": \"password123\"}")
                    )
            )
            @RequestBody Map<String, String> loginRequest) {
        return "로그인 성공"; // 실제 로그인 처리는 Security 필터에서 수행
    }


    @Operation(
            summary = "비밀번호 변경",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 변경 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"currentPassword\": \"password1234\", \"newPassword\": \"1q2w3e4r!\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "비밀번호 변경 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "비밀번호가 성공적으로 변경되었습니다"))),
            @ApiResponse(responseCode = "4XX", description = "비밀번호 변경 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "새 비밀번호는 현재 비밀번호와 다르게 설정해주세요")))
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordDto changePasswordDto) {
        Long userId = userAuthService.getUserIdFromToken(request);

        ChangePasswordResponse changePasswordResponse = userAuthService.changePassword(userId, changePasswordDto);

        if (changePasswordResponse.isSuccess()) {
            return ResponseEntity.ok(changePasswordResponse.getMessage());
        } else {
            return ResponseEntity.badRequest().body(changePasswordResponse.getMessage());
        }
    }

    @Operation(summary = "계정 삭제 (User 데이터 하드 삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "계정 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "계정이 성공적으로 삭제되었습니다"))),
            @ApiResponse(responseCode = "4XX", description = "계정 삭제 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "계정 삭제 요청시 body에는 아무것도 없이 요청",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{}"
                            )
                    )
            )
            HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userAuthService.deleteUser(userId);
        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다");
    }


    @Operation(
            summary = "추가 정보 기입 (일반 회원가입 직후 추가 정보 기입)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "추가 정보 기입 요청 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"spareTime\": 10, \"note\": \"이번엔 약속 꼭 지켜보자!\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2XX", description = "추가 정보 기입 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "추가 정보 기입 성공"))),
            @ApiResponse(responseCode = "4XX", description = "추가 정보 기입 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PutMapping("/{userId}/additional-info")
    public ResponseEntity<?> addInfo(
            @PathVariable Long userId, @RequestBody UserAdditionalInfoDto userAdditionalInfoDto) throws Exception {
        userAuthService.addInfo(userId, userAdditionalInfoDto);
        return ResponseEntity.ok("추가 정보 기입 성공");
    }

}
