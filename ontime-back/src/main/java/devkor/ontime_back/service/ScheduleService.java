package devkor.ontime_back.service;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final UserService userService;

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PreparationScheduleRepository preparationScheduleRepository;
    private final PreparationUserRepository preparationUserRepository;

    // userId 추출
    public Long getUserIdFromToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7); // "Bearer "를 제외한 토큰
        String refreshToken = request.getHeader("refresh-token");
        return jwtTokenProvider.extractUserId(accessToken).orElseThrow(() -> new RuntimeException("User ID not found in token"));
    }

    // 모든 약속 조회
    public List<ScheduleDto> showAllSchedules(Long userId) {
        List<Schedule> scheduleList = scheduleRepository.findAllyByUserId(userId);

        if (scheduleList.isEmpty()) {
            return Collections.emptyList();
        }

        return scheduleList.stream()
                .map(this::mapToDto)
                .toList();

    }

    // 특정 기간의 약속 조회
    public List<ScheduleDto> showSchedulesByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Schedule> periodScheduleList;

        if (startDate == null && endDate != null) {
            // StartDate가 null인 경우, EndDate 이전의 일정 모두 반환
            periodScheduleList = scheduleRepository.findAllByUserIdAndScheduleTimeBefore(userId, endDate);
        } else if (endDate == null && startDate != null) {
            // EndDate가 null인 경우, StartDate 이후의 일정 모두 반환
            periodScheduleList = scheduleRepository.findAllByUserIdAndScheduleTimeAfter(userId, startDate);
        } else if (startDate != null && endDate != null) {
            // StartDate와 EndDate 모두 존재하는 경우, 해당 기간의 일정 반환
            periodScheduleList = scheduleRepository.findAllByUserIdAndScheduleTimeBetween(
                    userId, startDate, endDate);
        } else {
            // StartDate와 EndDate가 모두 null인 경우, 모든 일정 반환
            periodScheduleList = scheduleRepository.findAllByUserId(userId);
        }

        return periodScheduleList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 약속 삭제
    public void deleteSchedule(UUID scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule with ID " + scheduleId + " not found."));
        // schedule을 만든 userId인지 확인
        if (!schedule.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to delete this schedule.");
        }
        scheduleRepository.delete(schedule);
    }

    // 약속 수정
    public void modifySchedule(Long userId, ScheduleModDto scheduleModDto) {
        // schedule 확인
        Schedule schedule = scheduleRepository.findById(scheduleModDto.getScheduleId()).orElseThrow(() -> new EntityNotFoundException("Schedule with ID " + scheduleModDto.getScheduleId() + " not found."));
        // schedule을 만든 userId인지 확인
        if (!schedule.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to delete this schedule.");
        }
        // place가 수정된 경우
        Place place = placeRepository.findByPlaceName(scheduleModDto.getPlaceName()).orElseGet(() -> {
            Place newPlace = new Place();
            newPlace.initPlaceName(scheduleModDto.getPlaceId(), scheduleModDto.getPlaceName());
            return placeRepository.save(newPlace);
        });

        // scheduleDto에서 수정
        schedule.updateSchedule(
                place,
                scheduleModDto.getScheduleName(),
                scheduleModDto.getMoveTime(),
                scheduleModDto.getScheduleTime(),
                scheduleModDto.getScheduleSpareTime(),
                scheduleModDto.getLatenessTime(),
                scheduleModDto.getScheduleNote());
    }

    // 약속 추가
    public void addSchedule(ScheduleAddDto scheduleAddDto, Long userId) {
        // user 확인
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found."));

        // place 확인
        // place가 없으면 추가가 여기?
        Place place = placeRepository.findByPlaceName(scheduleAddDto.getPlaceName()).orElseGet(() -> {
            Place newPlace = new Place();
            newPlace.initPlaceName(scheduleAddDto.getPlaceId(), scheduleAddDto.getPlaceName());
            return placeRepository.save(newPlace);
        });

        // schedule 추가
        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleAddDto.getScheduleId())
                .user(user)
                .place(place)
                .scheduleName(scheduleAddDto.getScheduleName())
                .moveTime(scheduleAddDto.getMoveTime())
                .scheduleTime(scheduleAddDto.getScheduleTime())
                .scheduleSpareTime(scheduleAddDto.getScheduleSpareTime())
                .scheduleNote(scheduleAddDto.getScheduleNote())
                .isChange(false)
                .isStarted(false)
                .build();

        scheduleRepository.save(schedule);
    }

    // 버튼 누름
    public void checkIsStarted(UUID scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule with ID " + scheduleId + " not found."));

        // schedule을 만든 userId인지 확인
        if (!schedule.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to delete this schedule.");
        }

        schedule.startSchedule();
    }

    // 지각 히스토리 반환
    public List<LatenessHistoryResponse> getLatenessHistory(Long userId) {
        return scheduleRepository.findLatenessHistoryByUserId(userId).stream()
                .map(schedule -> new LatenessHistoryResponse(
                        schedule.getScheduleId(),
                        schedule.getScheduleName(),
                        schedule.getScheduleTime(),
                        schedule.getLatenessTime()
                ))
                .toList();
    }

    // 지각 시간 업데이트
    public void updateLatenessTime(FinishPreparationDto finishPreparationDto) {
        UUID scheduleId = finishPreparationDto.getScheduleId();
        Integer latenessTime = finishPreparationDto.getLatenessTime();

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule with ID " + scheduleId + " not found."));

        schedule.setLatenessTime(latenessTime);
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void finishSchedule(Long userId, FinishPreparationDto finishPreparationDto) {
        updateLatenessTime(finishPreparationDto);
        userService.updatePunctualityScore(userId, finishPreparationDto.getLatenessTime());
    }
    public List<PreparationDto> getPreparations(Long userId, UUID scheduleId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found."));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + scheduleId));

        if (Boolean.TRUE.equals(schedule.getIsChange())) {
            return preparationScheduleRepository.findBySchedule(schedule).stream()
                    .map(preparationSchedule -> new PreparationDto(
                            preparationSchedule.getPreparationId(),
                            preparationSchedule.getPreparationName(),
                            preparationSchedule.getPreparationTime(),
                            preparationSchedule.getNextPreparation() != null
                                    ? preparationSchedule.getNextPreparation().getPreparationId()
                                    : null
                    ))
                    .collect(Collectors.toList());
        } else {
            return preparationUserRepository.findByUser(schedule.getUser()).stream()
                    .map(preparationUser -> new PreparationDto(
                            preparationUser.getPreparationId(),
                            preparationUser.getPreparationName(),
                            preparationUser.getPreparationTime(),
                            preparationUser.getNextPreparation() != null
                                    ? preparationUser.getNextPreparation().getPreparationId()
                                    : null
                    ))
                    .collect(Collectors.toList());
        }
    }

    private ScheduleDto mapToDto(Schedule schedule) {
        return new ScheduleDto(
                schedule.getScheduleId(),
                schedule.getPlace().getPlaceName(),
                schedule.getScheduleName(),
                schedule.getMoveTime(),
                schedule.getScheduleTime(),
                schedule.getScheduleSpareTime(),
                schedule.getScheduleNote(),
                schedule.getLatenessTime()
        );
    }

}
