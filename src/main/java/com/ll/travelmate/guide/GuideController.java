package com.ll.travelmate.guide;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guide")
public class GuideController {
    private final GuideService guideService;

    @GetMapping("/reading")
    public ResponseEntity<Object> readGuide(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideDto guideDto = guideService.readGuide(customMember.getTravelUserId());

        return new ResponseEntity<>(guideDto, HttpStatus.OK);
    }
}
