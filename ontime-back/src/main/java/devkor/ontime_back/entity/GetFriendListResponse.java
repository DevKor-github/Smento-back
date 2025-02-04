package devkor.ontime_back.entity;

import devkor.ontime_back.dto.FriendDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetFriendListResponse {

    private List<FriendDto> friendsList;

}
