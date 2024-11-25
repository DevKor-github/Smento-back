package devkor.ontime_back.repository;

import devkor.ontime_back.entity.PreparationSchedule;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PreparationScheduleRepository extends JpaRepository<PreparationSchedule, UUID> {
    List<PreparationSchedule> findBySchedule(Schedule schedule);
    void deleteBySchedule(Schedule schedule); // 메서드 선언
}
