package devkor.ontime_back.service;

import devkor.ontime_back.dto.FinishPreparationDto;
import devkor.ontime_back.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreparationService {
    private final UserService userService;
    private final ScheduleService scheduleService;

    @Transactional
    public void finishPreparation(Long userId, FinishPreparationDto finishPreparationDto) {
        scheduleService.updateLatenessTime(finishPreparationDto);
        userService.updatePunctualityScore(userId, finishPreparationDto.getLatenessTime());
    }
}
