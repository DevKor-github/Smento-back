package devkor.ontime_back.controller;

import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.service.UserAuthService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserSignUpTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService userAuthService;

    private static Long userId;

    @Test
    public void testSignUp() throws Exception {
        // Given
        Mockito.when(userAuthService.signUp(Mockito.any(UserSignUpDto.class))).thenReturn(userId);

        // When & Then
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password1234\",\"name\":\"junbeom\",\"userSettingId\":\"292aceb4-8d02-4094-b83c-b74acf2976a6\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다. 추가 정보를 기입해주세요( {userId}/additional-info )"));
    }
}
