package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Time;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; // 이메일

    private String password; // 비밀번호

    private String nickname; // 닉네임

    private String imageUrl; // 프로필 이미지

    @Column(nullable = false, length = 30)
    private String name; // 이름

    private Time spareTime; // 여유시간

    @Lob // 대용량 텍스트 필드
    @Column(columnDefinition = "TEXT") // 명시적으로 TEXT 타입으로 정의
    private String note; // 주의사항

    private Float score; // 성실도 점수


//    자체 로그인에서는 추가 정보인 age, city들을 회원가입 화면에서 입력
//    OAuth2 로그인은 추가 정보인 age, city를 로그인 시 따로 받지 X
//    이후에 OAuth2 로그인 구현 시에 로그인이 성공하면 추가 정보를 입력하는 폼으로 이동하도록 구현

//    @Enumerated(EnumType.STRING)
//    private Role role;

//    @Enumerated(EnumType.STRING)
//    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // refreshToken

    // 유저 권한 설정
//    public void authorizeUser() {
//        this.role = Role.USER;
//    }

    // 비밀번호 암호화
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateNickname(String updateNickname) {
        this.nickname = updateNickname;
    }

    public void updatePassword(String updatePassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(updatePassword);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}