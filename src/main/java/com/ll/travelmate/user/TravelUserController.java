package com.ll.travelmate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel-user")
@RequiredArgsConstructor
public class TravelUserController {
    private final TravelUserService travelUserService;

    @PostMapping("/create")
    // request를 받을때는 순서는 상관 없지만, dto에 있는 이름 그대로 키를 설정해야 한다.
    public ResponseEntity<Object> createUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        System.out.println(userRegistrationRequest.getTravelUserDto().toString());
        System.out.println(userRegistrationRequest.getMemberDto().toString());
        TravelUserDto createdUserDto = travelUserService.createUserAndMember(
                userRegistrationRequest.getTravelUserDto(),
                userRegistrationRequest.getMemberDto());
        // member도 자동으로 생기게 만들어야함.
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

}
