package devkor.ontime_back.service;

import devkor.ontime_back.dto.PreparationDto;
import devkor.ontime_back.entity.PreparationSchedule;
import devkor.ontime_back.entity.PreparationUser;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.PreparationScheduleRepository;
import devkor.ontime_back.repository.PreparationUserRepository;
import devkor.ontime_back.repository.ScheduleRepository;
import devkor.ontime_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PreparationScheduleService {
    private final PreparationScheduleRepository preparationScheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Long getUserIdFromToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7); // "Bearer "를 제외한 토큰
        return jwtTokenProvider.extractUserId(accessToken).orElseThrow(() -> new RuntimeException("User ID not found in token"));
    }

    public void makePreparationSchedules(Long userId, UUID scheduleId, List<PreparationDto> preparationDtoList) {
        handlePreparationSchedules(userId, scheduleId, preparationDtoList, false);
    }

    public void updatePreparationSchedules(Long userId, UUID scheduleId, List<PreparationDto> preparationDtoList) {
        handlePreparationSchedules(userId, scheduleId, preparationDtoList, true);
    }

    private void handlePreparationSchedules(Long userId, UUID scheduleId, List<PreparationDto> preparationDtoList, boolean shouldDelete) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + scheduleId));

        if (!schedule.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " is not authorized to modify this schedule.");
        }

        if (shouldDelete) {
            preparationScheduleRepository.deleteBySchedule(schedule);
        }

        schedule.changePreparationSchedule();
        scheduleRepository.save(schedule);

        Map<UUID, PreparationSchedule> preparationMap = new HashMap<>();

        List<PreparationSchedule> preparationSchedules = preparationDtoList.stream()
                .map(dto -> {
                    PreparationSchedule preparation = new PreparationSchedule(
                            dto.getPreparationId(),
                            schedule,
                            dto.getPreparationName(),
                            dto.getPreparationTime(),
                            null);
                    preparationMap.put(dto.getPreparationId(), preparation);
                    return preparation;
                })
                .collect(Collectors.toList());

        preparationScheduleRepository.saveAll(preparationSchedules);

        preparationDtoList.stream()
                .filter(dto -> dto.getNextPreparationId() != null)
                .forEach(dto -> {
                    PreparationSchedule current = preparationMap.get(dto.getPreparationId());
                    PreparationSchedule nextPreparation = preparationMap.get(dto.getNextPreparationId());
                    if (nextPreparation != null) {
                        current.updateNextPreparation(nextPreparation);
                    }
                });

        preparationScheduleRepository.saveAll(preparationSchedules);
    }
}
