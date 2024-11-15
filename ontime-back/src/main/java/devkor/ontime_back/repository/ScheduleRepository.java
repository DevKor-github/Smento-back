package devkor.ontime_back.repository;


import devkor.ontime_back.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findAllyByUserId(Long userId);

    void deleteById(UUID scheduleId);

    List<Schedule> findAllByUserIdAndScheduleTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // 특정 시간 범위 내에 시작되는 약속 조회 (예: 다음 날 또는 당일 약속)
    @Query("SELECT s FROM Schedule s WHERE s.scheduleTime BETWEEN :start AND :end")
    List<Schedule> findSchedulesBetween(LocalDateTime start, LocalDateTime end);

    // 특정 시간에 시작하는 약속 조회 (예: 5분 후 약속)
    @Query("SELECT s FROM Schedule s WHERE FUNCTION('HOUR', s.scheduleTime) = :hour AND FUNCTION('MINUTE', s.scheduleTime) = :minute")
    List<Schedule> findSchedulesStartingAt(int hour, int minute);
}
