package devkor.ontime_back.global.oauth.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.dto.OAuthGoogleRequestDto;
import devkor.ontime_back.dto.OAuthGoogleUserDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.SocialType;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final UserRepository userRepository;
    private final GoogleLoginService googleLoginService;


    public GoogleLoginFilter(String defaultFilterProcessesUrl, GoogleLoginService googleLoginService, UserRepository userRepository) {
        super(defaultFilterProcessesUrl);
        this.googleLoginService = googleLoginService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthGoogleRequestDto oAuthGoogleRequestDto = objectMapper.readValue(request.getInputStream(), OAuthGoogleRequestDto.class);
        OAuthGoogleUserDto oAuthGoogleUserInfo = googleLoginService.getUserInfoFromAccessToken(oAuthGoogleRequestDto.getAccessToken());

        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, oAuthGoogleUserInfo.getId());


        if (existingUser.isPresent()) {
            return googleLoginService.handleLogin(oAuthGoogleRequestDto, existingUser.get(), response);
        } else {
            return googleLoginService.handleRegister(oAuthGoogleRequestDto, oAuthGoogleUserInfo, response);
        }
    }





    // 인증 성공 처리
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("구글 로그인 성공");
        SecurityContextHolder.getContext().setAuthentication(authResult);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 인증 실패 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.warn("구글 로그인 실패");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"status\":\"error\", \"message\":\"Authentication failed\"}");
    }

}
