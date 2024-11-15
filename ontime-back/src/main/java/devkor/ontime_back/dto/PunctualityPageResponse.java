package devkor.ontime_back.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PunctualityPageResponse {
    private float punctualityScore;
    private List<LatenessHistoryResponse> latenessHistory;
    private List<ScheduleHistoryResponse> scheduleHistory;

    public PunctualityPageResponse(float punctualityScore, List<LatenessHistoryResponse> latenessHistory, List<ScheduleHistoryResponse> scheduleHistory) {
        this.punctualityScore = punctualityScore;
        this.latenessHistory = latenessHistory;
        this.scheduleHistory = scheduleHistory;
    }
}
