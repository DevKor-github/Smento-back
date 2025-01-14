package devkor.ontime_back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import devkor.ontime_back.ControllerTestSupport;
import devkor.ontime_back.TestSecurityConfig;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
class UserAuthControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입 즉, 신규 유저를 등록한다.")
    @Test
    void signUp() throws Exception {
        // given
        UserSignUpDto request = UserSignUpDto.builder()
                .email("user@example.com")
                .name("junbeom")
                .password("password123")
                .userSettingId(UUID.fromString("a304cde3-8ee9-4054-971a-300aacc2177c"))
                .build();

        User mockUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .name("junbeom")
                .password("password123")
                .role(Role.USER)
                .build();

        when(userAuthService.signUp(any(UserSignUpDto.class))).thenReturn(mockUser);

        // when // then
        mockMvc.perform(
                        post("/sign-up")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다. 추가 정보를 기입해주세요( {userId}/additional-info )"));
     }
}