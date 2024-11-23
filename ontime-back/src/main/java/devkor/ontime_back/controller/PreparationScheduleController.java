package devkor.ontime_back.controller;

import devkor.ontime_back.dto.PreparationDto;
import devkor.ontime_back.service.PreparationScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/preparationschedule")
@RequiredArgsConstructor
public class PreparationScheduleController {

    private final PreparationScheduleService preparationScheduleService;

    @PostMapping("/create/{scheduleId}")
    public ResponseEntity<Void> createPreparationSchedule(HttpServletRequest request, @PathVariable UUID scheduleId, @RequestBody List<PreparationDto> preparationDtoList) {
        Long userId = preparationScheduleService.getUserIdFromToken(request);

        preparationScheduleService.makePreparationSchedules(userId, scheduleId, preparationDtoList);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modify/{scheduleId}")
    public ResponseEntity<Void> modifyPreparationUser(HttpServletRequest request, @PathVariable UUID scheduleId, @RequestBody List<PreparationDto> preparationDtoList) {
        Long userId = preparationScheduleService.getUserIdFromToken(request);

        preparationScheduleService.updatePreparationSchedules(userId, scheduleId, preparationDtoList);
        return ResponseEntity.noContent().build();
    }

}
