package devkor.ontime_back.repository;

import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID> {
    Optional<Place> findByPlaceName(String placeName);
}
