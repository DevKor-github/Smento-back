package devkor.ontime_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class PreparationUser {
    @Id
    private UUID preparationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, length = 30)
    private String preparationName;

    private Integer preparationTime;

    @ManyToOne
    @JoinColumn(name = "next_preparation_id")
    private PreparationUser nextPreparation;

    public PreparationUser(UUID preparationId, User user, String preparationName, Integer preparationTime, PreparationUser preparationUser) {
        this.preparationId = preparationId;
        this.user = user;
        this.preparationName = preparationName;
        this.preparationTime = preparationTime;
        this.nextPreparation = preparationUser;
    }

    public void updateNextPreparation(PreparationUser nextPreparation) {
        this.nextPreparation = nextPreparation;
    }

    public void clearNextPreparation() {
        this.nextPreparation = null;
    }
}
