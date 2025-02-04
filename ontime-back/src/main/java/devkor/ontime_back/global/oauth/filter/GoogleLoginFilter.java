package devkor.ontime_back.global.oauth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public GoogleLoginFilter(String defaultFilterProcessesUrl, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        super(defaultFilterProcessesUrl);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthGoogleUserDto oAuthGoogleUserDto = objectMapper.readValue(request.getInputStream(), OAuthGoogleUserDto.class);

        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, oAuthGoogleUserDto.getSub());
        log.info(existingUser.toString());

        if (existingUser.isPresent()) {
            return handleLogin(existingUser.get(), response);
        } else {
            return handleRegister(oAuthGoogleUserDto, response);
        }
    }

    private Authentication handleLogin(User user, HttpServletResponse response) throws IOException {

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        jwtTokenProvider.updateRefreshToken(user.getEmail(), refreshToken);
        jwtTokenProvider.sendAccessToken(response, accessToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = String.format(
                "{\"message\": \"%s\", \"role\": \"%s\"}",
                user.getRole().name().equals("GUEST")
                        ? "유저의 ROLE이 GUEST이므로 온보딩API를 호출해 온보딩을 진행해야합니다."
                        : "로그인에 성공하였습니다.",
                user.getRole().name()
        );

        response.getWriter().write(responseBody);
        response.getWriter().flush();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
    }

    private Authentication handleRegister(OAuthGoogleUserDto oAuthGoogleUserDto, HttpServletResponse response) throws IOException {
        User newUser = User.builder()
                .socialType(SocialType.GOOGLE)
                .socialId(oAuthGoogleUserDto.getSub())
                .email(oAuthGoogleUserDto.getEmail())
                .name(oAuthGoogleUserDto.getName())
                .imageUrl(oAuthGoogleUserDto.getPicture())
                .role(Role.GUEST)
                .build();

        User savedUser = userRepository.save(newUser);

        String accessToken = jwtTokenProvider.createAccessToken(newUser.getEmail(), newUser.getId());
        jwtTokenProvider.sendAccessToken(response, accessToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = String.format(
                "{\"message\": \"%s\", \"role\": \"%s\"}",
                "회원가입이 완료되었습니다. ROLE이 GUEST이므로 온보딩이 필요합니다.",
                savedUser.getRole().name()
        );

        response.getWriter().write(responseBody);
        response.getWriter().flush();

        return new UsernamePasswordAuthenticationToken(newUser, null, Collections.singletonList(new SimpleGrantedAuthority(newUser.getRole().name())));
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
