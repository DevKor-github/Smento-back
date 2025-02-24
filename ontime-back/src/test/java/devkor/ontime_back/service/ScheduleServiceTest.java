package devkor.ontime_back.service;

import devkor.ontime_back.dto.FinishPreparationDto;
import devkor.ontime_back.dto.LatenessHistoryResponse;
import devkor.ontime_back.entity.Schedule;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.ScheduleRepository;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.response.GeneralException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        scheduleRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("지각 히스토리 조회에 성공한다")
    @Test
    void getLatenessHistory(){
        // given(유저, 스케줄1,2,3 데이터 하드저장)
        //      (비즈니스 로직에 따르면 스케줄 추가되면 지각시간은 -1로 자동으로 초기화됨.)
        //      (                      이후 스케줄이 종료되면 지각시간이 0 or 양수로 업데이트됨.)
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule1 = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(3)
                .user(addedUser)
                .build();

        Schedule addedSchedule2 = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe6"))
                .scheduleName("생일파티")
                .scheduleTime(LocalDateTime.of(2025, 1, 12, 21, 0))
                .latenessTime(1)
                .user(addedUser)
                .build();

        Schedule addedSchedule3 = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe7"))
                .scheduleName("Ontime 출시일")
                .scheduleTime(LocalDateTime.of(2025, 2, 14, 00, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();

        scheduleRepository.save(addedSchedule1);
        scheduleRepository.save(addedSchedule2);
        scheduleRepository.save(addedSchedule3);

        // when
        List<LatenessHistoryResponse> latenessHistory = scheduleService.getLatenessHistory(addedUser.getId());

        // then
        assertThat(latenessHistory).hasSize(2)
                .extracting("scheduleId", "scheduleName", "scheduleTime", "latenessTime")
                .containsExactlyInAnyOrder(
                        tuple(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"), "을사년 새해인줄 알았지?", LocalDateTime.of(2025, 1, 1, 0, 0), 3),
                        tuple(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe6"), "생일파티", LocalDateTime.of(2025, 1, 12, 21, 0), 1)
                );
    }

    @DisplayName("지각 시간 업데이트에 성공한다.")
    @Test
    void updateLatenessTime(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();
        scheduleRepository.save(addedSchedule);

        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .latenessTime(1)
                .build();

        // when
        scheduleService.updateLatenessTime(finishPreparationDto);

        // then
        assertThat(scheduleRepository.findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .get().getLatenessTime()).isEqualTo(1);
    }

    @DisplayName("지각 시간 업데이트할 때, 잘못된 schedulId를 DTO에 담아 요청하는 경우 예외가 발생한다.")
    @Test
    void updateLatenessTimeWithWrongScheduleId(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();
        scheduleRepository.save(addedSchedule);

        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe6"))
                .latenessTime(1)
                .build();

        // when // then
        assertThatThrownBy(() -> scheduleService.updateLatenessTime(finishPreparationDto))
                .isInstanceOf(GeneralException.class)
                .hasMessage("해당 약속이 존재하지 않습니다.");
    }

    @DisplayName("약속을 종료해 지각시간과 성실도점수 업데이트에 성공한다.")
    @Test
    void finishSchedule(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();
        scheduleRepository.save(addedSchedule);

        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .latenessTime(1)
                .build();

        // when
        scheduleService.finishSchedule(addedUser.getId(), finishPreparationDto);

        // then
        assertThat(scheduleRepository.findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .get().getLatenessTime()).isEqualTo(1);
        assertThat(userRepository.findById(addedUser.getId()).get().getPunctualityScore()).isEqualTo(0f);
    }

    @DisplayName("약속을 종료할 때, 잘못된 유저id를 인자로 넘기는 경우 예외가 발생한다.")
    @Test
    void finishScheduleWithWrongUserId(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();
        scheduleRepository.save(addedSchedule);

        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .latenessTime(1)
                .build();

        // when // then
        assertThatThrownBy(() -> scheduleService.finishSchedule(addedUser.getId() + 1, finishPreparationDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 id입니다.");
    }

    @DisplayName("약속을 종료할 때, 잘못된 scheduleId를 인자로 넘기는 경우 예외가 발생한다.")
    @Test
    void finishScheduleWithWrongScheduleId(){
        // given
        User addedUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password1234"))
                .name("junbeom")
                .punctualityScore(-1f)
                .scheduleCountAfterReset(0)
                .latenessCountAfterReset(0)
                .build();
        userRepository.save(addedUser);

        Schedule addedSchedule = Schedule.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe5"))
                .scheduleName("을사년 새해")
                .scheduleTime(LocalDateTime.of(2025, 1, 1, 0, 0))
                .latenessTime(-1)
                .user(addedUser)
                .build();
        scheduleRepository.save(addedSchedule);

        FinishPreparationDto finishPreparationDto = FinishPreparationDto.builder()
                .scheduleId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afe6"))
                .latenessTime(1)
                .build();

        // when // then
        assertThatThrownBy(() -> scheduleService.finishSchedule(addedUser.getId(), finishPreparationDto))
                .isInstanceOf(GeneralException.class)
                .hasMessage("해당 약속이 존재하지 않습니다.");
    }


}