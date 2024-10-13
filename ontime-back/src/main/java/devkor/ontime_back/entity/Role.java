package devkor.ontime_back.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

//    OAuth2 첫 로그인 시, Role을 Guest로 설정
//    추가 정보 입력 시 User로 업데이트

    GUEST("ROLE_GUEST"), USER("ROLE_USER");

    private final String key;
}