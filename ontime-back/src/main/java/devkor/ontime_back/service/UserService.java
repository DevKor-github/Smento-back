package devkor.ontime_back.service;

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

}
