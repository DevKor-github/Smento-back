package devkor.ontime_back.repository;

import devkor.ontime_back.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {
    Optional<UserSetting> findByUserId(Long userId);
    Optional<UserSetting> findByUserSettingId(UUID userSettingId);
}
