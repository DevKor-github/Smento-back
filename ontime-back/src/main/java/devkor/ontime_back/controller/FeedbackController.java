package devkor.ontime_back.controller;

import devkor.ontime_back.dto.FeedbackAddDto;
import devkor.ontime_back.service.FeedbackService;
import devkor.ontime_back.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final UserAuthService userAuthService;

    @PostMapping("/add")
    public ResponseEntity<String> saveFeedback(HttpServletRequest request, @RequestBody FeedbackAddDto feedbackAddDto) {
        Long userId = userAuthService.getUserIdFromToken(request);

        feedbackService.saveFeedback(userId, feedbackAddDto);
        return ResponseEntity.ok("피드백이 성공적으로 저장되었습니다.");
    }
}