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
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final UserRepository userRepository;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userAuthService.signUp(userSignUpDto);
        return "회원가입 성공";
    }


    @Operation(summary = "User Login", description = "로그인 요청을 통해 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = String.class)))
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
            @RequestBody Map<String, String> loginRequest
    ) {
        return "로그인 요청 성공"; // 실제 로그인 처리는 Security 필터에서 수행
    }


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

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userAuthService.deleteUser(userId);
        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다");
    }


    @Operation(summary = "JWT 테스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT 테스트 성공", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    @PutMapping("/{id}/additional-info")
    public ResponseEntity<?> addInfo(@PathVariable Long id, @RequestBody UserAdditionalInfoDto userAdditionalInfoDto) throws Exception {
        userAuthService.addInfo(id, userAdditionalInfoDto);
        return ResponseEntity.ok("추가 정보 기입 성공");
    }

}
