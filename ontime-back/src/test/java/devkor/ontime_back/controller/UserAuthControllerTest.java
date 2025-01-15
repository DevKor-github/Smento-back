package devkor.ontime_back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import devkor.ontime_back.ControllerTestSupport;
import devkor.ontime_back.TestSecurityConfig;
import devkor.ontime_back.dto.ChangePasswordDto;
import devkor.ontime_back.dto.UserAdditionalInfoDto;
import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.entity.Role;
import devkor.ontime_back.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다. 추가 정보를 기입해주세요( {userId}/additional-info )"))
                .andExpect(jsonPath("$.data.userId").value(1));
     }

     @DisplayName("(회원가입 직후) 추가 정보 기입에 성공한다")
     @Test
     void addInfo() throws Exception {
         // given
         UserAdditionalInfoDto userAdditionalInfoDto = UserAdditionalInfoDto.builder()
                 .spareTime(10)
                 .note("내 인생에 더 이상의 지각은 없다!!!")
                 .build();

         // 원래 addInfo는 User객체를 반환하지만 컨트롤러에서는 반환값을 사용하지 않으므로 null로 설정하였음.
         when(userAuthService.addInfo(any(Long.class), any(UserAdditionalInfoDto.class))).thenReturn(null);

         // when // then
         mockMvc.perform(
                        put("/1/additional-info")
                                .content(objectMapper.writeValueAsString(userAdditionalInfoDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("추가 정보가 성공적으로 기입되었습니다!"));
      }

    @DisplayName("비밀번호 변경에 성공한다")
    @Test
    void changePassword() throws Exception {
        // given
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password1234")
                .newPassword("password12345")
                .build();

        // 원래 changePassword는 User객체를 반환하지만 컨트롤러에서는 반환값을 사용하지 않으므로 null로 설정하였음.
        when(userAuthService.changePassword(any(Long.class), any(ChangePasswordDto.class))).thenReturn(null);

        // when // then
        mockMvc.perform(
                        put("/change-password")
                                .content(objectMapper.writeValueAsString(changePasswordDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다!"));
    }

    @DisplayName("계정 삭제에 성공한다")
    @Test
    void deleteUser() throws Exception {
        // given
        // 원래 changePassword는 삭제된 유저의 Id를 반환하지만 컨트롤러에서는 반환값을 사용하지 않으므로 null로 설정하였음.
        when(userAuthService.deleteUser(any(Long.class))).thenReturn(null);

        // when // then
        mockMvc.perform(
                        delete("/user/delete")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("계정이 성공적으로 삭제되었습니다!"));
    }
}