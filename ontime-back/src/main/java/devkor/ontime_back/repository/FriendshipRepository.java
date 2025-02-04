package devkor.ontime_back.repository;

import devkor.ontime_back.entity.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendShip, Long> {

    Optional<FriendShip> findByFriendShipId(UUID friendShipId);

    // Friendship에 어차피 User방향의 연관관계가 없으므로 조인 자체가 필요없어 페치조인 적용할 필요도 없음
    @Query("SELECT f FROM FriendShip f WHERE f.requesterId = :userId AND f.acceptStatus = :accepted")
    List<FriendShip> findByRequesterIdAndAcceptStatus(@Param("userId") Long userId, @Param("accepted") String accepted);

    // Friendship에 어차피 User방향의 연관관계가 없으므로 조인 자체가 필요없어 페치조인 적용할 필요도 없음
    @Query("SELECT f FROM FriendShip f WHERE f.receiverId = :userId AND f.acceptStatus = :accepted")
    List<FriendShip> findByReceiverIdAndAcceptStatus(Long userId, String accepted);
}
