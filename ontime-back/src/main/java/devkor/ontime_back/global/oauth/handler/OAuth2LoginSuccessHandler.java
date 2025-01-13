package devkor.ontime_back.global.oauth.handler;

import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.global.oauth.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            if(oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getEmail(), oAuth2User.getUserId());
            } else {
                response.sendRedirect("/login/oauth2/code/google");

            }
        } catch (Exception e) {
            throw e;
        }

    }
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        try {
            // accessToken과 refreshToken 생성
            String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getEmail(), oAuth2User.getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken();

            // 응답 형식 설정 (JSON)
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);

            // accessToken과 refreshToken을 JSON 형태로 반환
            response.getWriter().write("{\"accessToken\":\"" + accessToken + "\", \"refreshToken\":\"" + refreshToken + "\"}");
        } catch (Exception e) {
            log.error("로그인 성공 처리 중 오류 발생: ", e);
        }
    }
}