package devkor.ontime_back.global.oauth.service;

import devkor.ontime_back.dto.OAuthGoogleUserDto;
import devkor.ontime_back.dto.OAuthKakaoUserDto;
import devkor.ontime_back.dto.SocialUserSignupDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.SocialType;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2Service {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> signup(String token, SocialUserSignupDto socialUserSignupDto) {
        Long userId = jwtTokenProvider.extractUserId(token).orElseThrow(() -> new RuntimeException("User ID not found in token"));

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.updateAdditionalInfo(socialUserSignupDto.getSpareTime(), socialUserSignupDto.getNote());
            user.authorizeUser();
            userRepository.save(user);
            return ResponseEntity.ok("User information updated successfully.");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found.");
        }
    }

    public String handleGoogleLoginOrRegister(OAuthGoogleUserDto oAuthGoogleUserDto, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, oAuthGoogleUserDto.getSub());

        if (existingUser.isPresent()) {
            googleLogin(existingUser.get(), response);
            return "login";
        } else {
            googleRegister(oAuthGoogleUserDto, response);
            return "register";
        }
    }

    // 구글 회원가입
    private void googleRegister(OAuthGoogleUserDto oAuthGoogleUserDto, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, oAuthGoogleUserDto.getSub());
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User newUser = User.builder()
                .socialType(SocialType.GOOGLE)
                .socialId(oAuthGoogleUserDto.getSub())
                .email(oAuthGoogleUserDto.getEmail())
                .name(oAuthGoogleUserDto.getName())
                .imageUrl(oAuthGoogleUserDto.getPicture())
                .role(Role.GUEST)
                .build();
        userRepository.save(newUser);

        String accessToken = jwtTokenProvider.createAccessToken(newUser.getEmail(), newUser.getId());
        jwtTokenProvider.sendAccessToken(response, accessToken);
    }

    // 구글 로그인
    private void googleLogin(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        jwtTokenProvider.updateRefreshToken(user.getEmail(), refreshToken);
        jwtTokenProvider.sendAccessToken(response, accessToken);
    }

    // 카카오 로그인 또는 회원가입 처리
    public String handleKakaoLoginOrRegister(OAuthKakaoUserDto oAuthKakaoUserDto, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, oAuthKakaoUserDto.getId());

        if (existingUser.isPresent()) {
            kakaoLogin(existingUser.get(), response);
            return "login";
        } else {
            kakaoRegister(oAuthKakaoUserDto, response);
            return "register";
        }
    }

    // 카카오 회원가입
    private void kakaoRegister(OAuthKakaoUserDto oAuthKakaoUserDto, HttpServletResponse response) {
        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, oAuthKakaoUserDto.getId());
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User newUser = User.builder()
                .socialType(SocialType.KAKAO)
                .socialId(oAuthKakaoUserDto.getId())
                .name(oAuthKakaoUserDto.getProfile().getNickname())
                .imageUrl(oAuthKakaoUserDto.getProfile().getProfileImageUrl())
                .role(Role.GUEST)
                .build();
        userRepository.save(newUser);

        String accessToken = jwtTokenProvider.createAccessToken(newUser.getEmail(), newUser.getId());
        jwtTokenProvider.sendAccessToken(response, accessToken);
    }

    // 카카오 로그인
    private void kakaoLogin(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        jwtTokenProvider.updateRefreshToken(user.getEmail(), refreshToken);
        jwtTokenProvider.sendAccessToken(response, accessToken);
    }
}