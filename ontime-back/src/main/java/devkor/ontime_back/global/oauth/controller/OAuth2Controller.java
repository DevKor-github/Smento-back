package devkor.ontime_back.global.oauth.controller;

import devkor.ontime_back.dto.OAuthGoogleUserDto;
import devkor.ontime_back.dto.OAuthKakaoUserDto;
import devkor.ontime_back.dto.SocialUserSignupDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.SocialType;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.oauth.service.OAuth2Service;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.response.ApiResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


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
            @ApiResponse(responseCode = "4XX", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody SocialUserSignupDto socialUserSignupDto) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        return oauth2Service.signup(token, socialUserSignupDto);
    }

    @Operation(
            summary = "구글 소셜 로그인/회원가입",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "구글 회원정보 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\n \"sub\": \"101129251579261120097\",\n \"name\": \"홍길동\",\n \"given_name\": \"길동\",\n \"family_name\": \"홍\",\n \"picture\": \"http://dfsklafj;ewoai.jpg\",\n \"email\": \"hong0000@gmail.com\",\n \"email_verified\": true }"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구글 로그인/회원가입 성공 (로그인시 data : login, 회원가입시 data : register", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"status\": \"success\",\n  \"code\": \"200\",\n  \"message\": \"OK \",\n  \"data\": \"login\"}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(이메일이 이미 존재할 경우, 이름이 이미 존재할 경우 다르게 출력)")))
    })
    @PostMapping("/google/registerOrLogin")
    public ResponseEntity<ApiResponseForm<String>> googleRegisterOrLogin(@RequestBody OAuthGoogleUserDto oAuthGoogleUserDto, HttpServletResponse response) {
        String result = oauth2Service.handleGoogleLoginOrRegister(oAuthGoogleUserDto, response);
        if ("login".equals(result)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponseForm.success("login"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseForm.success("register"));
        }
    }

    @Operation(
            summary = "카카오 소셜 로그인/회원가입",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카카오 회원정보 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\n \"id\": \"4803687123\", \n \"profile\": {\n \"nickname\": \"김철수\", \n \"thumbnail_image_url\": \"http://dfsklafj;ewoai.jpg\", \n \"profile_image_url\": \"http://dfsklafj;ewoai.jpg\", \n\"is_default_image\": false, \n \"is_default_nickname\": false\n }\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인/회원가입 성공 (로그인시 data : login, 회원가입시 data : register", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{\n  \"status\": \"success\",\n  \"code\": \"200\",\n  \"message\": \"OK \",\n  \"data\": \"login\"}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(이메일이 이미 존재할 경우, 이름이 이미 존재할 경우 다르게 출력)")))
    })
    @PostMapping("/kakao/registerOrLogin")
    public ResponseEntity<ApiResponseForm<String>> kakaoRegisterOrLogin(@RequestBody OAuthKakaoUserDto oAuthKakaoUserDto, HttpServletResponse response) {
        String result = oauth2Service.handleKakaoLoginOrRegister(oAuthKakaoUserDto, response);
        if ("login".equals(result)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponseForm.success("login"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseForm.success("register"));
        }
    }

}