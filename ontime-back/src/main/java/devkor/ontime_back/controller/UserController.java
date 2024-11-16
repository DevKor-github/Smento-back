package devkor.ontime_back.controller;

import devkor.ontime_back.dto.LatenessHistoryResponse;
import devkor.ontime_back.dto.PunctualityPageResponse;
import devkor.ontime_back.dto.ScheduleHistoryResponse;
import devkor.ontime_back.dto.UpdatePunctualityScoreDto;
import devkor.ontime_back.service.ScheduleService;
import devkor.ontime_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ScheduleService scheduleService;

    // 성실도 페이지 데이터 반환
    @GetMapping("/{userId}/punctuality")
    public PunctualityPageResponse getPunctualityPage(@PathVariable Long userId) {
        float punctualityScore = userService.getPunctualityScore(userId); // -1 or float 0~100 반환
        List<LatenessHistoryResponse> latenessHistory = scheduleService.getLatenessHistory(userId);
        List<ScheduleHistoryResponse> scheduleHistory = scheduleService.getScheduleHistory(userId);

        return new PunctualityPageResponse(punctualityScore, latenessHistory, scheduleHistory);
    }

    // 성실도 점수 초기화
    @PutMapping("/{userId}/reset-punctuality")
    public ResponseEntity<String> resetPunctualityScore(@PathVariable Long userId) {
        userService.resetPunctualityScore(userId);

        return ResponseEntity.ok("성실도 점수가 초기화 되었습니다!");
    }

    // 성실도 점수 업데이트(약속 준비과정 종료시 request)
    @PutMapping("/{userId}/update-punctuality")
    public ResponseEntity<String> updatePunctualityScore(
            @PathVariable Long userId,
            @RequestBody UpdatePunctualityScoreDto updatePunctualityScoreDto) {

        userService.updatePunctualityScore(userId, updatePunctualityScoreDto.getLatenessTime());

        return ResponseEntity.ok("성실도 점수가 성공적으로 업데이트 되었습니다!");
    }
}

