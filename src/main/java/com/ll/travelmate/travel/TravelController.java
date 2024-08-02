package com.ll.travelmate.travel;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelController {
    private final TravelService travelService;
    @PostMapping("/addition")
    public ResponseEntity<Object> addTravel(@RequestBody TravelDto travelDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelDto addtravelDto = travelService.addTravel(customMember.getTravelUserId(), travelDto);

        if(addtravelDto == null)
            return new ResponseEntity<>("시작 날짜가 이미 지났습니다.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(addtravelDto, HttpStatus.OK);
    }

    @GetMapping("/read/all")
    public ResponseEntity<Object> readAllTravel(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<TravelDto> travelDtos = travelService.readAllTravel(customMember.getTravelUserId());
        return new ResponseEntity<>(travelDtos, HttpStatus.OK);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Object> readAllTravel(@PathVariable Long id, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelDto travelDto = travelService.readTravel(id);

        if (travelDto == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(travelDto, HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<Object> updateTravel(@RequestBody TravelDto travelDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        TravelDto updatetravelDto = travelService.updateTravel(customMember.getTravelUserId(), travelDto);
        if (updatetravelDto == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(updatetravelDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteTravel(@RequestBody TravelDto travelDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        travelService.deleteTravel(customMember.getTravelUserId(), travelDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
