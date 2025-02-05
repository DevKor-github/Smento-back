package devkor.ontime_back.applelogin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.dto.OAuthAppleUserDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.SocialType;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.global.jwt.JwtUtils;
import devkor.ontime_back.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";

    @Value("${apple.client.id}")
    private String clientId;

    @Value("${apple.team.id}")
    private String teamId;

    @Value("${apple.login.key}")
    private String keyId;
    private static final String REDIRECT_URI = "https://ontime.devkor.club/oauth2/apple/callback";

    @Value("${apple.client.secret}")
    private String privateKeyPath;

    private String issuer = "https://appleid.apple.com";

    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final RestTemplate restTemplate = new RestTemplate();

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Authentication registerOrLogin(String identityToken, String authorizationCode, String fullName, HttpServletResponse response) throws Exception {
        Claims tokenClaims = verifyIdentityToken(identityToken);
        if (tokenClaims.getSubject() == null) {
            throw new IllegalStateException("Apple 로그인 검증 실패");
        }

        String appleUserId = tokenClaims.getSubject();
        String email = tokenClaims.get("email", String.class);
        boolean isEmailVerified = Boolean.parseBoolean(tokenClaims.get("email_verified", String.class));

        OAuthAppleUserDto oAuthAppleUserDto = new OAuthAppleUserDto(appleUserId, email, isEmailVerified, fullName);

        // appleAccessToken, appleRefreshToken 반환
        // String appleAccessToken = getAppleAccessTokenAndRefreshToken(authorizationCode).getAccessToken();
        String appleRefreshToken = getAppleAccessTokenAndRefreshToken(authorizationCode).getRefreshToken();
        boolean isRevoked = checkAppleLoginRevoked(appleRefreshToken);
        if (isRevoked) {
            throw new IllegalStateException("Apple 로그인 철회됨: 사용자가 로그인 연결을 해제함");
        }

        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.APPLE, appleUserId);

        if (existingUser.isPresent()) {
            return handleLogin(existingUser.get(), response);
        } else {
            return handleRegister(oAuthAppleUserDto, response);
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

    private Authentication handleRegister(OAuthAppleUserDto oAuthAppleUserDto, HttpServletResponse response) throws IOException {
        User newUser = User.builder()
                .socialType(SocialType.GOOGLE)
                .socialId(oAuthAppleUserDto.getAppleUserId())
                .email(oAuthAppleUserDto.getEmail())
                .name(oAuthAppleUserDto.getFullName())
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

    // identitytoken 검증
    private Claims verifyIdentityToken(String identityToken) throws
            Exception {
        Map<String, String> headers = jwtUtils.parseHeaders(identityToken);
        // apple publickey
        ApplePublicKeyResponse applePublicKeyResponse = restTemplate.getForObject(APPLE_KEYS_URL, ApplePublicKeyResponse.class);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeyResponse);
        // claim
        Claims tokenClaims = jwtUtils.getTokenClaims(identityToken, publicKey);
        // iss 확인
        if (!issuer.equals(tokenClaims.getIssuer())) {
            throw new IllegalArgumentException("Invalid JWT: Issuer mismatch. Expected: " + issuer);
        }
        // aud 확인
        if (!clientId.equals(tokenClaims.getAudience())) {
            throw new IllegalArgumentException("Invalid JWT: Audience mismatch. Expected: " + clientId);
        }

        return tokenClaims;
    }

    // apple 서버로부터 accesstoken, refreshtoken 발급
    private AppleTokenResponse getAppleAccessTokenAndRefreshToken(String authCode) throws Exception {
        // clientSecret
        String clientSecret = generateClientSecret();

        String requestBody = String.format(
                "grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s",
                authCode, clientId, clientSecret, REDIRECT_URI);

        JsonNode response = restTemplate.postForObject(APPLE_TOKEN_URL, requestBody, JsonNode.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.treeToValue(response, AppleTokenResponse.class);
    }

    // clientsecret 생성
    private String generateClientSecret() throws Exception {

        // Private Key
        String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = java.util.Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey privateKey = kf.generatePrivate(spec);

        // now
        Date now = new Date();

        // clientSecret
        return Jwts.builder()
                .setHeaderParam("alg", "ES256")
                .setHeaderParam("kid", keyId)
                .setIssuer(teamId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 3600000))
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    // Apple 로그인 철회 감지
    public boolean checkAppleLoginRevoked(String appleRefreshToken) throws Exception {
        String clientSecret = generateClientSecret();

        String revokeUrl = "https://appleid.apple.com/auth/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = UriComponentsBuilder.newInstance()
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("token", appleRefreshToken)
                .queryParam("token_type_hint", "refresh_token")
                .build()
                .toString().substring(1);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    revokeUrl, HttpMethod.POST, requestEntity, String.class);

            return response.getStatusCode() != HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            return true;
        }
    }
}
