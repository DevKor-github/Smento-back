package devkor.ontime_back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String placeName;

    public Place initPlaceName(String placeName) {
        this.placeName = placeName;
        return this;
    }
}