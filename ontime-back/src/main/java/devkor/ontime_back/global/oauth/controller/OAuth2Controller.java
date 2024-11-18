package devkor.ontime_back.global.oauth.controller;

import devkor.ontime_back.dto.SocialUserSignupDto;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.oauth.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;
    private final JwtTokenProvider jwtTokenProvider;


    @Operation(
            summary = "소셜 회원가입 추가정보 기입",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "소셜 회원가입 추가정보 기입 JSON 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"spareTime\": 10, \"note\": \"주의사항\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가정보 기입 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "User information updated successfully."))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody SocialUserSignupDto socialUserSignupDto) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        return oauth2Service.signup(token, socialUserSignupDto);
    }


}