package devkor.ontime_back.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
@Transactional
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email"; // jwt claim으로 email 사용
    private static final String BEARER = "Bearer "; //'Authorization(Key) = Bearer {토큰} (Value)' 형식
    private static final String USER_ID_CLAIM = "userId";

    private final UserRepository userRepository;


    // accessToken 생성
    public String createAccessToken(String email, Long userId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .withClaim(USER_ID_CLAIM, userId)
                .sign(Algorithm.HMAC512(secretKey));
    }

    // refreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // accessToken header에 넣어서 전송
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    // accessToken + refreshToken header에 넣어서 전송
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("accesstoken: "+ accessToken + "refreshtoken" + refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    // header에서 refreshToken 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        // "x-refresh-token" 헤더에서 추출
        return Optional.ofNullable(request.getHeader("refresh-token"))
                .filter(refreshToken -> !refreshToken.isEmpty());
    }

    // header에서 accessToken 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    // accessToken에서 email 추출
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.as");
            return Optional.empty();
        }
    }

    // accessToken에서 userId 추출
    public Optional<Long> extractUserId(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(USER_ID_CLAIM)
                    .asLong());
        } catch (Exception e) {
            log.error("유효하지 않은 accessToken입니다.");
            return Optional.empty();
        }
    }

    // accessToken header 설정
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    // refreshToken header 설정
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    // refreshToken db에 업데이트
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> { throw new RuntimeException("일치하는 회원이 없습니다."); }
                );
    }

    // token 유효성 확인
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            log.info("유효한 토큰입니다.");
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    public String createExpiredAccessToken(String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() - 36000000)) // 현재 시간보다 1초 이전으로 설정
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createAccessTokenFromRefreshToken(String refreshToken) {
        // 1. refreshToken이 유효한지 확인
        if (!isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 2. refreshToken에서 이메일 추출
        Optional<String> emailOptional = extractEmail(refreshToken);
        if (emailOptional.isEmpty()) {
            throw new IllegalArgumentException("Could not extract email from refresh token");
        }

        String email = emailOptional.get();

        // 3. 이메일로 사용자 조회
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        // 4. 새로운 accessToken 생성
        String newAccessToken = createAccessToken(user.getEmail(), user.getId());

        // 5. 새로운 accessToken 반환
        return newAccessToken;
    }


}