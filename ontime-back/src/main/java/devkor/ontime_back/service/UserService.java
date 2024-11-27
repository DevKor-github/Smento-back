package devkor.ontime_back.service;

import devkor.ontime_back.dto.UpdateSpareTimeDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 성실도 점수 반환
    public Float getPunctualityScore(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getPunctualityScore();
    }

    // 성실도 점수 초기화
    public void resetPunctualityScore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.resetPunctualityScore(); // 점수 초기화
        userRepository.save(user);
    }

    public void updatePunctualityScore(Long userId, Integer latenessTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getPunctualityScore() == (float) -1) {
            // 초기화 이후 첫 약속
            user.setScheduleCountAfterReset(1);
            user.setLatenessCountAfterReset(latenessTime > 0 ? 1 : 0);
        } else {
            // 기존 성실도 점수가 존재 -> 약속 수와 지각 수 업데이트
            user.setScheduleCountAfterReset(user.getScheduleCountAfterReset() + 1);
            if (latenessTime > 0) {
                user.setLatenessCountAfterReset(user.getLatenessCountAfterReset() + 1);
            }
        }

        // 성실도 점수 계산
        int totalSchedules = user.getScheduleCountAfterReset();
        int lateSchedules = user.getLatenessCountAfterReset();
        float punctualityScore = (1 - ((float) lateSchedules / totalSchedules)) * 100;

        user.setPunctualityScore(punctualityScore);
        userRepository.save(user);
    }

    public void updateSpareTime(Long userId, UpdateSpareTimeDto updateSpareTimeDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateSpareTime(updateSpareTimeDto.getNewSpareTime());

        userRepository.save(user);
    }
}
