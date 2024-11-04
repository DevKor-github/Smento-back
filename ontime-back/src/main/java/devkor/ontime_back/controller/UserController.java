package devkor.ontime_back.controller;

import devkor.ontime_back.dto.UserSignUpDto;
import devkor.ontime_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return "회원가입 성공";
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

    //로그아웃 되는지 테스트(SecurityConfig 필터체인 메서드에서 logout시 리디렉트할 url 설정할 수 있음)
    @PostMapping("/logout-success")
    public String logoutSucess() {
        return "로그아웃 성공";
    }

}
