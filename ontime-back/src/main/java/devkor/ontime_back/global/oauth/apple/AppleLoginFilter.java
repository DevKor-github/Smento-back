package devkor.ontime_back.global.oauth.apple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.dto.AppleTokenResponseDto;
import devkor.ontime_back.dto.OAuthAppleRequestDto;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AppleLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final AppleLoginService appleLoginService;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    public AppleLoginFilter(String defaultFilterProcessesUrl, AppleLoginService appleLoginService, UserRepository userRepository) {
        super(defaultFilterProcessesUrl);
        this.appleLoginService = appleLoginService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthAppleRequestDto oAuthAppleRequestDto = objectMapper.readValue(request.getInputStream(), OAuthAppleRequestDto.class);

        try {
            // Apple Identity Token 검증
            Claims tokenClaims = appleLoginService.verifyIdentityToken(oAuthAppleRequestDto.getIdToken());
            if (tokenClaims.getSubject() == null) {
                throw new IllegalStateException("Apple 로그인 검증 실패");
            }

            String appleUserId = tokenClaims.getSubject();
            log.info("appleUserId: {}", appleUserId);
            String email = tokenClaims.get("email", String.class);

            String appleRefreshToken = appleLoginService.getAppleAccessTokenAndRefreshToken(oAuthAppleRequestDto.getAuthCode()).getRefreshToken();
            // socialRefreshtoken에 저장

            OAuthAppleUserDto oAuthAppleUserDto = new OAuthAppleUserDto(appleUserId, email, oAuthAppleRequestDto.getFullName());

            Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(SocialType.APPLE, appleUserId);

            if (existingUser.isPresent()) {
                return appleLoginService.handleLogin(existingUser.get(), response);
            } else {
                return appleLoginService.handleRegister(oAuthAppleUserDto, response);
            }

        } catch (Exception e) {
            log.error("Apple 로그인 실패: {}", e.getMessage(), e);
            throw new AuthenticationException("Apple 로그인 실패") {};
        }
    }

}
