package devkor.ontime_back.global.generallogin.handler;

import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    // 수정된 부분: User의 ID(PK)를 AccessToken 생성에 사용
                    String accessToken = jwtTokenProvider.createAccessToken(email, user.getId());

                    String refreshToken = jwtTokenProvider.createRefreshToken(); // RefreshToken 발급

                    // 수정된 부분: 응답 헤더에 AccessToken, RefreshToken 실어서 응답
                    jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);

                    // 수정된 부분: RefreshToken을 User 엔티티에 업데이트 후 저장
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);

                    log.info("로그인에 성공하였습니다. 이메일 : {}", email);
                    log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
                    log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
                });
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
