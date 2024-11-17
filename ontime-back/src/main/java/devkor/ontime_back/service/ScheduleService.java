package devkor.ontime_back.service;

import devkor.ontime_back.dto.*;
import devkor.ontime_back.entity.Place;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.global.jwt.JwtTokenProvider;
import devkor.ontime_back.repository.PlaceRepository;
import devkor.ontime_back.repository.ScheduleRepository;
import devkor.ontime_back.repository.UserRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

    // 오늘의 약속 조회
    public List<ScheduleDto> showTodaySchedules(Long userId) {
        LocalDateTime now = LocalDateTime.now(); // 현재 시간
        LocalDateTime endofToday = LocalDateTime.now()
                .plusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999); // 내일 자정 전까지의 시각

        List<Schedule> todayScheduleList = scheduleRepository.findAllByUserIdAndScheduleTimeBetween(userId, now, endofToday);

        return todayScheduleList.stream()
                .map(this::mapToDto)
                .toList();
    }

    // 이달의 약속 조회
    public List<ScheduleDto> showMonthSchedule(Long userId){
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1) // 이번 달의 첫째 날
                .withHour(0).withMinute(0).withSecond(0).withNano(0); // 00:00:00.000

        LocalDateTime endOfMonth = LocalDateTime.now()
                .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()) // 이번 달의 마지막 날
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999); // 23:59:59.999999999

        List<Schedule> monthScheduleList = scheduleRepository.findAllByUserIdAndScheduleTimeBetween(userId, startOfMonth, endOfMonth);

        return monthScheduleList.stream()
                .map(this::mapToDto)
                .toList();
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

    // 약속 히스토리 반환
    public List<ScheduleHistoryResponse> getScheduleHistory(Long userId) {
        return scheduleRepository.findAllByUserId(userId).stream()
                .map(schedule -> new ScheduleHistoryResponse(
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

    private ScheduleDto mapToDto(Schedule schedule) {
        return new ScheduleDto(
                schedule.getScheduleId(),
                schedule.getPlace().getPlaceName(),
                schedule.getScheduleName(),
                schedule.getMoveTime(),
                schedule.getScheduleTime(),
                schedule.getScheduleSpareTime(),
                schedule.getScheduleNote()
        );
    }

}
