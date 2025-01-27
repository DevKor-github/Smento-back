package devkor.ontime_back.controller;

import devkor.ontime_back.dto.FriendDto;
import devkor.ontime_back.dto.GetFriendshipRequesterResponse;
import devkor.ontime_back.dto.UpdateAcceptStatusDto;
import devkor.ontime_back.entity.User;
import devkor.ontime_back.entity.GetFriendListResponse;
import devkor.ontime_back.response.ApiResponseForm;
import devkor.ontime_back.service.FriendshipService;
import devkor.ontime_back.service.UserAuthService;
import devkor.ontime_back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendShipController {

    private final UserAuthService userAuthService;
    private final UserService userService;
    private final FriendshipService friendShipService;

    @PostMapping("/link/create") // 친구 추가 링크 생성
    public ResponseEntity<ApiResponseForm<String>> createFriendShipLink(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        String friendShipUUID =friendShipService.createFriendShipLink(userId);

        String message = "친구추가 링크 생성 성공. 반환되는 UUID로 친구추가 요청 데이터를 조회하고, 친구추가 요청을 수락하거나 거절할 수 있음.";
        return ResponseEntity.ok(ApiResponseForm.success(friendShipUUID, message));
    }

    @GetMapping("/add-requester/{uuid}") // 친구 추가 요청자 조회
    public ResponseEntity<ApiResponseForm<GetFriendshipRequesterResponse>> getFriendShipRequester(HttpServletRequest request, @PathVariable String uuid) {
        Long userId = userAuthService.getUserIdFromToken(request);

        User requester = friendShipService.getFriendShipRequester(userId, UUID.fromString(uuid));
        GetFriendshipRequesterResponse getFriendshipRequesterResponse = GetFriendshipRequesterResponse.builder()
                .requesterId(requester.getId())
                .requesterName(requester.getName())
                .requesterEmail(requester.getEmail())
                .build();

        String message = "친구추가 요청자 조회 성공. 친구추가 요청자의 ID 반환";
        return ResponseEntity.ok(ApiResponseForm.success(getFriendshipRequesterResponse, message));
    }

    @PostMapping("/update-status/{uuid}") // 친구 추가 요청 수락
    public ResponseEntity<ApiResponseForm<String>> updateAcceptStatus(HttpServletRequest request, @PathVariable String uuid, @RequestBody UpdateAcceptStatusDto updateAcceptStatusDto) {
        Long userId = userAuthService.getUserIdFromToken(request);

        friendShipService.updateAcceptStatus(userId, UUID.fromString(uuid), updateAcceptStatusDto.getAcceptStatus());

        String status = updateAcceptStatusDto.getAcceptStatus().equals("ACCEPTED") ? "수락" : "거절";
        String message = "친구추가 요청 " + status + " 성공";
        return ResponseEntity.ok(ApiResponseForm.success(null, message));
    }

    @GetMapping("/list") // 친구 목록 조회
    public ResponseEntity<ApiResponseForm<GetFriendListResponse>> getFriendList(HttpServletRequest request) {
        Long userId = userAuthService.getUserIdFromToken(request);

        List<FriendDto> friendsList = friendShipService.getFriendList(userId);
        GetFriendListResponse getFriendsListResponse = GetFriendListResponse.builder()
                .friendsList(friendsList)
                .build();

        String message = "친구 목록 조회 성공";
        return ResponseEntity.ok(ApiResponseForm.success(getFriendsListResponse, message));
    }

}
