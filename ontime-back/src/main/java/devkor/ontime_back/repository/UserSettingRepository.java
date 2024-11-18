package devkor.ontime_back.repository;

import devkor.ontime_back.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {
    Optional<UserSetting> findByUserId(Long userId);
}
