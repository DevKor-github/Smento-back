package devkor.ontime_back.controller;

import devkor.ontime_back.dto.ScheduleModDto;
import devkor.ontime_back.dto.UserSettingUpdateDto;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserSettingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-setting")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserAuthService userAuthService;
    private final UserSettingService userSettingService;

    @PutMapping("/update")
    public ResponseEntity<String> modifySchedule(HttpServletRequest request, @RequestBody UserSettingUpdateDto userSettingUpdateDto) {
        Long userId = userAuthService.getUserIdFromToken(request);

        userSettingService.updateSetting(userId, userSettingUpdateDto);

        return ResponseEntity.ok("사용자 앱 설정 업데이트 완료");
    }
}
