package com.ll.travelmate.user;

import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.request.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-user")
@RequiredArgsConstructor
public class TravelUserController {
    private final TravelUserService travelUserService;

    @PostMapping("/creation")
    // request를 받을때는 순서는 상관 없지만, dto에 있는 이름 그대로 키를 설정해야 한다.
    public ResponseEntity<Object> createUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        // 생성시 휴대폰 인증 부분도 생각해야할듯
        TravelUserDto createdUserDto = travelUserService.createUserAndMember(
                userRegistrationRequest.getTravelUserDto(),
                userRegistrationRequest.getMemberDto());

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PostMapping("/recommendation-list")
    public ResponseEntity<List<TravelUserDto>> getRecommendedList(@RequestBody List<Long> travelUserIds) {
        List<TravelUserDto> travelUserDtos = travelUserService.getTravelUsersByIds(travelUserIds);
        return new ResponseEntity<>(travelUserDtos, HttpStatus.OK);
    }

    @GetMapping("/reading")
    public ResponseEntity<Object> readUser(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelUserDto travelUserDto = travelUserService.readTravelUser(customMember);
        return new ResponseEntity<>(travelUserDto, HttpStatus.OK);
    }
}
