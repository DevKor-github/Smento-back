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

    @Query("SELECT s FROM Schedule s WHERE s.scheduleTime BETWEEN :startOfDay AND :endOfDay")
    List<Schedule> findSchedulesForTomorrow(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
