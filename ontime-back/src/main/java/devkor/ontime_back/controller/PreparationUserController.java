package devkor.ontime_back.controller;

import devkor.ontime_back.dto.PreparationDto;
import devkor.ontime_back.service.PreparationUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preparationuser")
@RequiredArgsConstructor
public class PreparationUserController {


    private final PreparationUserService preparationUserService;

    @PostMapping("/set/first")
    public ResponseEntity<Void> setFirstPreparationUser(HttpServletRequest request, @RequestBody List<PreparationDto> preparationDtoList) {
        Long userId = preparationUserService.getUserIdFromToken(request);

        preparationUserService.setFirstPreparationUser(userId, preparationDtoList);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/modify")
    public ResponseEntity<Void> modifyPreparationUser(HttpServletRequest request, @RequestBody List<PreparationDto> preparationDtoList) {
        Long userId = preparationUserService.getUserIdFromToken(request);

        preparationUserService.updatePreparationUsers(userId, preparationDtoList);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/show/all")
    public ResponseEntity<List<PreparationDto>> getAllPreparationUser(HttpServletRequest request) {
        Long userId = preparationUserService.getUserIdFromToken(request);

        List<PreparationDto> preparationUserList = preparationUserService.showAllPreparationUsers(userId);
        return ResponseEntity.ok(preparationUserList);

    }

}
