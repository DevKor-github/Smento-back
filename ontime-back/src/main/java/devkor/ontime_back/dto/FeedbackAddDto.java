package devkor.ontime_back.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
public class FeedbackAddDto {
    private UUID feedbackId;
    private String message;
}
