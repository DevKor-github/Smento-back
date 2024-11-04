package devkor.ontime_back.global.oauth.service;

import devkor.ontime_back.dto.UserSignupRequest;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> signup(String token, UserSignupRequest userSignupRequest) {
        // 토큰을 검증하고 이메일 추출
        Long userId = jwtTokenProvider.extractUserId(token).orElseThrow(() -> new RuntimeException("User ID not found in token"));


        // 이메일로 사용자 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 추가 정보 업데이트
            user.updateAdditionalInfo(userSignupRequest.getSpareTime(), userSignupRequest.getNote(), userSignupRequest.getScore());
            // Role 변경 (GUEST -> USER)
            user.authorizeUser();
            userRepository.save(user);
            return ResponseEntity.ok("User information updated successfully.");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found.");
        }
    }

}
