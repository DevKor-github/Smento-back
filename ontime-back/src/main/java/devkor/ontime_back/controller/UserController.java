package devkor.ontime_back.controller;

import devkor.ontime_back.dto.*;
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

    // 약속 준비 종료 이후 지각시간(Schedule 테이블), 성실도 점수(User 테이블) 업데이트
    @PutMapping("/{userId}/finish-preparation")
    public ResponseEntity<String> finishPreparation(
            @PathVariable Long userId,
            @RequestBody FinishPreparationDto finishPreparationDto) {

        scheduleService.updateLatenessTime(finishPreparationDto);
        userService.updatePunctualityScore(userId, finishPreparationDto.getLatenessTime());

        return ResponseEntity.ok("해당 약속의 지각시간과 해당 유저의 성실도점수가 성공적으로 업데이트 되었습니다!");
    }
}

