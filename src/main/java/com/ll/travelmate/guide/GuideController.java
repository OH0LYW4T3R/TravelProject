package com.ll.travelmate.guide;

import com.ll.travelmate.guidereview.GuideReviewDto;
import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/reading/{id}")
    public ResponseEntity<Object> readGuide(@PathVariable Long id, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideDto guideDto = guideService.readOtherGuide(id);

        if (guideDto == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(guideDto, HttpStatus.OK);
    }

    @GetMapping("/reading/my-review")
    public ResponseEntity<Object> readMyReview(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideReviewDto> guideReviewDtos = guideService.readMyReview(customMember.getTravelUserId());

        return new ResponseEntity<>(guideReviewDtos, HttpStatus.OK);
    }

    @GetMapping("/reading/review/{id}")
    public ResponseEntity<Object> readOtherReview(@PathVariable Long id, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideReviewDto> guideReviewDtos = guideService.readOtherReview(id);

        if (guideReviewDtos == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(guideReviewDtos, HttpStatus.OK);
    }
}
