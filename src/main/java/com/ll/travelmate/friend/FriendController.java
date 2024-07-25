package com.ll.travelmate.friend;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;
    @PostMapping("/additional")
    public ResponseEntity<Object> addFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        FriendDto addFriendDto = friendService.addFriend(customMember.getTravelUserId(), friendDto);

        if (addFriendDto != null)
            return new ResponseEntity<>(addFriendDto, HttpStatus.CREATED);
        else // 정보가 올바르지 않거나, 이미 친구 요청을 한 상태인 경우
            return new ResponseEntity<>("정보가 올바르지 않거나, 이미 친구 요청 혹은 거절된 상태입니다.", HttpStatus.valueOf(400));
    }

    @PatchMapping("/acceptance")
    public ResponseEntity<Object> acceptFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        FriendDto acceptFriendDto = friendService.acceptFriend(customMember.getTravelUserId(), friendDto);

        if (acceptFriendDto != null)
            return new ResponseEntity<>(acceptFriendDto, HttpStatus.OK);
        else // 상대방이 탈퇴했거나, 이미 서로 친추 된 상태인 경우
            return new ResponseEntity<>("친구요청을 할수 없는 상태거나, 이미 친구추가가 되었습니다.", HttpStatus.valueOf(400));
    }

    @PatchMapping("/refusal")
    public ResponseEntity<Object> refuseFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        FriendDto refuseFriendDto = friendService.refuseFriend(customMember.getTravelUserId(), friendDto);

        if (refuseFriendDto != null)
            return new ResponseEntity<>(refuseFriendDto, HttpStatus.OK);
        else // 상대방이 탈퇴했거나, 이미 거절한 경우
            return new ResponseEntity<>("친구 거절을 할수 없는 상태거나, 이미 거절이 되었습니다.", HttpStatus.valueOf(400));
    }

    // 친구 삭제 부분도 만들기
    @DeleteMapping("/friend-remove")
    public ResponseEntity<Object> removeFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        friendService.removeFriend(customMember.getTravelUserId(), friendDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/request-list")
    public ResponseEntity<Object> readRequestList(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<FriendDto> friendDtos = friendService.readList(customMember.getTravelUserId(), FriendStatus.request);

        if (friendDtos != null)
            return new ResponseEntity<>(friendDtos, HttpStatus.OK);
        else // 로그인 정보가 올바르지 않거나
            return new ResponseEntity<>("Invalid request", HttpStatus.valueOf(400));
    }

    @GetMapping("/standby-list")
    public ResponseEntity<Object> readStandByList(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();

        List<FriendDto> friendDtos = friendService.readList(customMember.getTravelUserId(), FriendStatus.standby);

        if (friendDtos != null)
            return new ResponseEntity<>(friendDtos, HttpStatus.OK);
        else // 로그인 정보가 올바르지 않거나
            return new ResponseEntity<>("Invalid request", HttpStatus.valueOf(400));
    }

    @GetMapping("/friend-list")
    public ResponseEntity<Object> readFriendList(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();

        List<FriendDto> friendDtos = friendService.readList(customMember.getTravelUserId(), FriendStatus.acceptance);

        if (friendDtos != null)
            return new ResponseEntity<>(friendDtos, HttpStatus.OK);
        else // 로그인 정보가 올바르지 않거나
            return new ResponseEntity<>("Invalid request", HttpStatus.valueOf(400));
    }

    @GetMapping("/refusal-list")
    public ResponseEntity<Object> readRefusalList(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();

        List<FriendDto> friendDtos = friendService.readList(customMember.getTravelUserId(), FriendStatus.refuse);

        if (friendDtos != null)
            return new ResponseEntity<>(friendDtos, HttpStatus.OK);
        else // 로그인 정보가 올바르지 않거나
            return new ResponseEntity<>("Invalid request", HttpStatus.valueOf(400));
    }
}
