package devkor.ontime_back.global.oauth.controller;

import devkor.ontime_back.dto.SocialUserSignupDto;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.oauth.service.OAuth2Service;
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

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody SocialUserSignupDto socialUserSignupDto) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        return oauth2Service.signup(token, socialUserSignupDto);
    }


}