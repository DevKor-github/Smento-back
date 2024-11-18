package devkor.ontime_back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Operation(
            summary = "이용약관 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이용약관 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
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
            @ApiResponse(responseCode = "200", description = "이용약관 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "이용약관 ~~~ 입니다."))),
            @ApiResponse(responseCode = "4XX", description = "이용약관 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/terms")
    public String getTerms() {
        return "이용약관 ~~~입니다.";
    }

    @Operation(
            summary = "개인정보처리방침 조회",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "개인정보처리방침 조회 요청 JSON 데이터는 없음. 헤더에 토큰만 있으면 됨",
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
            @ApiResponse(responseCode = "200", description = "개인정보처리방침 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "이용약관 ~~~ 입니다."))),
            @ApiResponse(responseCode = "4XX", description = "개인정보처리방침 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "실패 메세지(정확히 어떤 메세지인지는 모름)")))
    })
    @GetMapping("/privacy")
    public String getPrivacyPolicy() {
        return "개인정보처리방침 ~~~입니다.";
    }
}
