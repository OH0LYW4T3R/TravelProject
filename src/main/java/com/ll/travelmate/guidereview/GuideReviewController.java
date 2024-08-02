package com.ll.travelmate.guidereview;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class GuideReviewController {
    private final GuideReviewService guideReviewService;
    @PostMapping("/addition")
    public ResponseEntity<Object> addReview(@RequestBody ReviewRequestDto reviewRequestDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideReviewDto guideReviewDto = guideReviewService.addReview(customMember.getTravelUserId(), reviewRequestDto);

        if (guideReviewDto == null)
            return new ResponseEntity<>("이미 작성된 계획이거나, 잘못된 접근입니다.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(guideReviewDto, HttpStatus.OK);
    }

    @GetMapping("/reading")
    public ResponseEntity<Object> readReview(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideReviewDto> guideReviewDtos = guideReviewService.readReview(customMember.getTravelUserId());

        return new ResponseEntity<>(guideReviewDtos, HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<Object> readReview(@RequestBody GuideReviewDto guideReviewDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideReviewDto reviewRequestDto = guideReviewService.updateReview(customMember.getTravelUserId(), guideReviewDto);

        if (reviewRequestDto == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.NOT_FOUND);

        return  new ResponseEntity<>(reviewRequestDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteReview(@RequestBody GuideReviewDto guideReviewDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        guideReviewService.deleteReview(customMember.getTravelUserId(), guideReviewDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
