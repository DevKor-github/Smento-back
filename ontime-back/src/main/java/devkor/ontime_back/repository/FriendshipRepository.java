package devkor.ontime_back.repository;

import devkor.ontime_back.entity.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendShip, Long> {

    Optional<FriendShip> findByFriendShipId(UUID friendShipId);
}
