package devkor.ontime_back.controller;

import devkor.ontime_back.dto.ScheduleAddDto;
import devkor.ontime_back.dto.ScheduleDto;
import devkor.ontime_back.dto.ScheduleModDto;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 전체 약속 조회
    @Operation(summary = "Get all schedules", description = "Fetch all schedules based on some filters")
    @GetMapping("/show/all")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showAllSchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 오늘의 약속 조회
    @GetMapping("/show/today")
    public ResponseEntity<List<ScheduleDto>> getTodaySchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showTodaySchedules(userId);
        return ResponseEntity.ok(schedules);
    }

    // 이달의 약속 조회
    @GetMapping("/show/month")
    public ResponseEntity<List<ScheduleDto>> getMonthSchedules(HttpServletRequest request) {
        Long userId = scheduleService.getUserIdFromToken(request);

        List<ScheduleDto> schedules = scheduleService.showMonthSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

    // 약속 삭제
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.deleteSchedule(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }

    // 약속 수정
    @PutMapping("/modify")
    public ResponseEntity<Void> modifySchedule(HttpServletRequest request, @RequestBody ScheduleModDto scheduleModDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.modifySchedule(userId, scheduleModDto);
        return ResponseEntity.ok().build();
    }

    // 약속 추가

    @PostMapping("/add")
    public ResponseEntity<Void> addSchedule(HttpServletRequest request, @RequestBody ScheduleAddDto scheduleAddDto) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.addSchedule(scheduleAddDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 시작 버튼 누름
    @PatchMapping("/start/{scheduleId}")
    public ResponseEntity<Void> isStartedSchedule(HttpServletRequest request, @PathVariable UUID scheduleId) {
        Long userId = scheduleService.getUserIdFromToken(request);

        scheduleService.checkIsStarted(scheduleId, userId);
        return ResponseEntity.ok().build();
    }




}
