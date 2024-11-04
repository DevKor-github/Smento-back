package devkor.ontime_back.global.oauth.controller;

import devkor.ontime_back.dto.UserSignupRequest;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.oauth.service.OAuth2Service;
import devkor.ontime_back.repository.UserRepository;
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
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        return oauth2Service.signup(request);
    }

}