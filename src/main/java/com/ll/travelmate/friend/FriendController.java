package com.ll.travelmate.friend;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 정리
* 나 -> 상대 (request) => 행동 : 삭제
* 상대 -> 나 (standby) => 행동 : 수락, 거절
* 수락시
* 나 -> 상대 (acceptance) => 행동 : 삭제, 차단
* 상대 -> 나 (acceptance) => 행동 : 삭제, 차단
* 거절시
* 상대 -> 나 (refuse) => 행동 : 삭제
* 차단시 (친추된 상태여야함)
* 나 -> 상대 (acceptance) => 행동 : 삭제, 차단
* 상대 -> 나 (block) => 행동 : 차단 해제
* */

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

    @PatchMapping("/block")
    public ResponseEntity<Object> blockedFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        FriendDto blockedFriendDto = friendService.blockedFriend(customMember.getTravelUserId(), friendDto);

        if (blockedFriendDto != null)
            return new ResponseEntity<>(blockedFriendDto, HttpStatus.OK);
        else // 차단이 불가능한 상황이거나, 이미 차단한 상대입니다.
            return new ResponseEntity<>("차단이 불가능한 상태이거나, 이미 차단한 상대입니다.", HttpStatus.valueOf(400));
    }

    @PatchMapping("/unblock")
    public ResponseEntity<Object> unblockedFriend(@RequestBody FriendDto friendDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        FriendDto unblockedFriendDto = friendService.unblockedFriend(customMember.getTravelUserId(), friendDto);

        if (unblockedFriendDto != null)
            return new ResponseEntity<>(unblockedFriendDto, HttpStatus.OK);
        else // 차단 해제가 불가능한 상태거나, 친구가 이미 친구를 삭제하였습니다.
            return new ResponseEntity<>("차단 해제가 불가능한 상태 또는 차단해제를 이미 했거나, 친구가 이미 친구를 삭제하였습니다.", HttpStatus.valueOf(400));
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

    @GetMapping("/blocked-list")
    public ResponseEntity<Object> readBlockedList(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();

        List<FriendDto> friendDtos = friendService.readList(customMember.getTravelUserId(), FriendStatus.block);

        if (friendDtos != null)
            return new ResponseEntity<>(friendDtos, HttpStatus.OK);
        else // 로그인 정보가 올바르지 않거나
            return new ResponseEntity<>("Invalid request", HttpStatus.valueOf(400));
    }
}
