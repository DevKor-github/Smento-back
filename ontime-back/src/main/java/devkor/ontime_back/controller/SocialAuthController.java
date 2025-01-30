package devkor.ontime_back.controller;

import devkor.ontime_back.dto.OAuthGoogleUserDto;
import devkor.ontime_back.dto.OAuthKakaoUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class SocialAuthController {

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
                            example = "{\n  \"message\": \"유저의 ROLE이 GUEST이므로 온보딩API를 호출해 온보딩을 진행해야합니다.\",\n  \"role\": \"GUEST\"}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(이메일이 이미 존재할 경우, 이름이 이미 존재할 경우 다르게 출력)")))
    })
    @PostMapping("/google/registerOrLogin")
    public String googleRegisterOrLogin(@RequestBody OAuthGoogleUserDto oAuthGoogleUserDto, HttpServletResponse response) {
        return "구글 로그인/회원가입 성공"; // 로그인 처리는 필터에서 적용
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
                            example = "{\n  \"message\": \"유저의 ROLE이 GUEST이므로 온보딩API를 호출해 온보딩을 진행해야합니다.\",\n  \"role\": \"GUEST\"}"
                    )
            )),
            @ApiResponse(responseCode = "4XX", description = "실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(이메일이 이미 존재할 경우, 이름이 이미 존재할 경우 다르게 출력)")))
    })
    @PostMapping("/kakao/registerOrLogin")
    public String kakaoRegisterOrLogin(@RequestBody OAuthKakaoUserDto oAuthKakaoUserDto, HttpServletResponse response) {
        return "카카오 로그인/회원가입 성공"; // 로그인 처리는 필터에서 적용
    }

}