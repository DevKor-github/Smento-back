package devkor.ontime_back.global.jwt;

import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.SecureRandom;

import jakarta.servlet.ServletException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;


// "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtTokenProvider.extractRefreshToken(request) // header에서 refreshToken 추출
                .filter(jwtTokenProvider::isTokenValid)
                .orElse(null);

        if (refreshToken != null) { // accessToken 만료 -> refreshToken 존재
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        if (refreshToken == null) { // accessToken X: 403 에러 / accessToken O: 인증 성공
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    // refreshToken로 검색 후 refreshToken, accessToken 재발급 후 전송
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken) // refreshToken으로 유저 찾기
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user); // refreshToken 재발급
                    jwtTokenProvider.sendAccessAndRefreshToken(response, jwtTokenProvider.createAccessToken(user.getEmail(), user.getId()),
                            reIssuedRefreshToken); // accessToken도 생성 후 전송
                });
    }

    // refreshToken 재발급
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtTokenProvider.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    // accessToken 확인 후 인증 확인
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtTokenProvider.extractAccessToken(request)
                .filter(jwtTokenProvider::isTokenValid)
                .ifPresent(accessToken -> {
                    jwtTokenProvider.extractEmail(accessToken)
                            .ifPresent(email -> userRepository.findByEmail(email)
                                    .ifPresent(this::saveAuthentication));

                    jwtTokenProvider.extractUserId(accessToken)
                            .ifPresent(userId -> log.info("추출된 userId: {}", userId));
                });

        filterChain.doFilter(request, response);
    }

    // 인증 허가
    public void saveAuthentication(User myUser) {
        String password = myUser.getPassword();
        if (password == null) {
            password = generateRandomPassword(12); // 소셜 로그인 유저의 비밀번호 임의로 설정
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 비밀번호 랜덤 생성
    private String generateRandomPassword(int length) {
        final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+[]{}|;:,.<>?";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHAR_SET.length());
            password.append(CHAR_SET.charAt(index));
        }

        return password.toString();
    }
}