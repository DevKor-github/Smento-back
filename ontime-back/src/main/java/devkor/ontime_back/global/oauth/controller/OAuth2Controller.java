package devkor.ontime_back.global.oauth.controller;

import devkor.ontime_back.dto.UserSignupRequest;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.oauth.service.OAuth2Service;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody UserSignupRequest userSignupRequest) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        return oauth2Service.signup(token, userSignupRequest);
    }

}