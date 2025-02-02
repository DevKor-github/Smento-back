package devkor.ontime_back.repository;


import devkor.ontime_back.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findAllyByUserId(Long userId);

    void deleteById(UUID scheduleId);

    List<Schedule> findAllByUserIdAndScheduleTimeBefore(Long userId, LocalDateTime endDate);
    List<Schedule> findAllByUserIdAndScheduleTimeAfter(Long userId, LocalDateTime startDate);
    List<Schedule> findAllByUserIdAndScheduleTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // 특정 시간 범위 내에 시작되는 약속 조회 (예: 다음 날 또는 당일 약속)
    @Query("SELECT s FROM Schedule s WHERE s.scheduleTime BETWEEN :start AND :end")
    List<Schedule> findSchedulesBetween(LocalDateTime start, LocalDateTime end);

    // 특정 시간에 시작하는 약속 조회 (예: 5분 후 약속)
    @Query("SELECT s FROM Schedule s WHERE FUNCTION('HOUR', s.scheduleTime) = :hour AND FUNCTION('MINUTE', s.scheduleTime) = :minute")
    List<Schedule> findSchedulesStartingAt(int hour, int minute);

    // 지각 히스토리 조회(페치조인을 했었는데 본 메서드를 사용하는 서비스메서드에서 user를 참조하지 않아서 필요없음)
    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId AND s.latenessTime > 0")
    List<Schedule> findLatenessHistoryByUserId(@Param("userId") Long userId);

    // 약속 히스토리 조회
    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId")
    List<Schedule> findAllByUserId(@Param("userId") Long userId);

}
