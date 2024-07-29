package com.ll.travelmate.user;

import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.request.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/travel-user")
@RequiredArgsConstructor
public class TravelUserController {
    private final TravelUserService travelUserService;
    private final RestTemplate restTemplate;
    private final TravelUserRepository travelUserRepository;
    @PostMapping("/creation")
    // request를 받을때는 순서는 상관 없지만, dto에 있는 이름 그대로 키를 설정해야 한다.
    public ResponseEntity<Object> createTravelUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        // 생성시 휴대폰 인증 부분도 생각해야할듯
        TravelUserDto createdUserDto = travelUserService.createUserAndMember(
                userRegistrationRequest.getTravelUserDto(),
                userRegistrationRequest.getMemberDto());

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @GetMapping("/recommendation-list")
    public ResponseEntity<Object> getRecommendedList(Authentication auth) {
        String uri = "persona/recommend/";
        String keyName = "list";
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<TravelUserDto> travelUserDtos = new ArrayList<>();

        try {
            travelUserDtos = travelUserService.findRecommendedTravelUsers(uri + String.valueOf(customMember.getTravelUserId()), keyName);
        } catch (Exception exception) {
            return new ResponseEntity<>("Server Error", HttpStatus.valueOf(500));
        }

        return new ResponseEntity<>(travelUserDtos, HttpStatus.OK);
    }

    @GetMapping("/reading")
    public ResponseEntity<Object> readTravelUser(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelUserDto travelUserDto = travelUserService.readTravelUser(customMember);
        return new ResponseEntity<>(travelUserDto, HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<Object> updateTravelUser(Authentication auth, @RequestBody TravelUserDto travelUserDto) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelUserDto updateTravelUserDto = travelUserService.updateTravelUser(customMember.getTravelUserId(), travelUserDto);
        return new ResponseEntity<>(updateTravelUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/deletion")
    public ResponseEntity<Object> deleteTravelUser(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        // 상대방에게 걸려온 친추도 없애는 로직 추가
        travelUserService.deleteTravelUser(customMember.getTravelUserId());
        return new ResponseEntity<>("Delete Success", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recommend/test")
    public ResponseEntity<Object> testRecommend(Authentication auth) {
        List<TravelUserDto> travelUserDtos = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        ids.add(5L);

        for (Long id : ids) {
            travelUserDtos.add(travelUserService.convertToDto(travelUserRepository.findById(id).get(), null));
        }

        return new ResponseEntity<>(travelUserDtos, HttpStatus.OK);
    }
}
