package devkor.ontime_back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class Place {

    @Id
    private UUID placeId;

    private String placeName;

    public Place initPlaceName(UUID placeId, String placeName) {
        this.placeId = placeId;
        this.placeName = placeName;
        return this;
    }
}