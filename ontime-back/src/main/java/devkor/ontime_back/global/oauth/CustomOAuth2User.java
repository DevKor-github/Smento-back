package devkor.ontime_back.global.oauth;

import devkor.ontime_back.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private Long userId;
    private String email;
    private Role role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            Long userId, String email, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}