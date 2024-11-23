package devkor.ontime_back.repository;

import devkor.ontime_back.entity.PreparationUser;
import devkor.ontime_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PreparationUserRepository extends JpaRepository<PreparationUser, UUID> {

    List<PreparationUser> findByUser(User user);

    @Query("SELECT pu FROM PreparationUser pu WHERE pu.user = :user AND pu NOT IN " +
            "(SELECT p.nextPreparation FROM PreparationUser p WHERE p.nextPreparation IS NOT NULL)")
    Optional<PreparationUser> findFirstPreparationUserByUser(User user);

    void deleteByUser(User user); // 메서드 선언
}
