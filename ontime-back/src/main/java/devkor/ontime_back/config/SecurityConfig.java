package devkor.ontime_back.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import devkor.ontime_back.global.jwt.JwtAuthenticationFilter;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
//import devkor.ontime_back.global.login.CustomJsonUsernamePasswordAuthenticationFilter;
//import devkor.ontime_back.global.login.LoginFailureHandler;
//import devkor.ontime_back.global.login.LoginService;
//import devkor.ontime_back.global.login.LoginSuccessHandler;
import devkor.ontime_back.global.oauth.CustomOAuth2UserService;
import devkor.ontime_back.global.oauth.handler.OAuth2LoginFailureHandler;
import devkor.ontime_back.global.oauth.handler.OAuth2LoginSuccessHandler;
import devkor.ontime_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Collections;
import java.util.List;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
//    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**", "/sign-up").permitAll()
                        .requestMatchers("/jwt-test").permitAll()
                        .requestMatchers("/oauth2/sign-up").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .addFilterBefore(jwtAuthenticationProcessingFilter(), LogoutFilter.class);

        return http.build();


    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(loginService);
//        return new ProviderManager(provider);
//    }
//
//    @Bean
//    public LoginSuccessHandler loginSuccessHandler() {
//        return new LoginSuccessHandler(jwtService, userRepository);
//    }
//
//    @Bean
//    public LoginFailureHandler loginFailureHandler() {
//        return new LoginFailureHandler();
//    }
//
//    @Bean
//    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
//        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
//                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
//        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
//        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
//        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
//        return customJsonUsernamePasswordLoginFilter;
//    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
        return jwtAuthenticationFilter;
    }
}