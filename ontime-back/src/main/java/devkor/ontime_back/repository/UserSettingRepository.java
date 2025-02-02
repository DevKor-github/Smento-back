package devkor.ontime_back.repository;

import devkor.ontime_back.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {

    // 페치조인 적용
    @Query("SELECT us FROM UserSetting us JOIN FETCH us.user WHERE us.user.id = :userId")
    Optional<UserSetting> findByUserId(@Param("userId") Long userId);


    Optional<UserSetting> findByUserSettingId(UUID userSettingId);
}
