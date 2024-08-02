package com.ll.travelmate.guideproposal;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guide-proposal")
public class GuideProposalController {
    private final GuideProposalService guideProposalService;
    private final GuideProposalRepository guideProposalRepository;

    @PostMapping("/creation")
    public ResponseEntity<Object> createGuideProposal(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideProposalDto createGuideProposalDto = guideProposalService.createGuideProposal(guideProposalDto, customMember.getTravelUserId());

        return new ResponseEntity<>(createGuideProposalDto, HttpStatus.OK);
    }

    // RUD 만들어야함.
    @GetMapping("/reading/all")
    public ResponseEntity<Object> readingAllGuideProposal(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideProposalDto> guideProposalDtos = guideProposalService.readAllGuideProposal(customMember.getTravelUserId());
        return new ResponseEntity<>(guideProposalDtos, HttpStatus.OK);
    }

    @GetMapping("/reading/guide-proposal/recommend")
    public ResponseEntity<Object> readingAllRecommendGuideProposal(
            @RequestParam(name = "area") String area,
            Authentication auth
    ) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideProposalDto> guideProposalDtos = guideProposalService.readRecommendGuideProposal(customMember.getTravelUserId(), area);
         return new ResponseEntity<>(guideProposalDtos, HttpStatus.OK);
    }

    @GetMapping("/reading/purchase-status")
    public ResponseEntity<Object> readingPurchaseStatusGuideProposal(
            @RequestParam(name = "purchaseStatus", defaultValue = "registration") String purchaseStatus,
            Authentication auth
    ) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<GuideProposalDto> guideProposalDtos = guideProposalService.readPurchaseStatusGuideProposal(customMember.getTravelUserId(), PurchaseStatus.valueOf(purchaseStatus));
        return new ResponseEntity<>(guideProposalDtos ,HttpStatus.OK);
    }

    @GetMapping("/reading/{id}")
    public ResponseEntity<Object> readingGuideProposal(@PathVariable Long id) {
        GuideProposalDto guideProposalDto = guideProposalService.readGuideProposal(id);

        if (guideProposalDto == null)
            return new ResponseEntity<>("찾을려는 정보가 없습니다.",HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(guideProposalDto, HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<Object> readingGuideProposal(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideProposalDto updateGuideProposalDto = guideProposalService.updateGuideProposal(customMember.getTravelUserId(), guideProposalDto);

        if (updateGuideProposalDto == null)
            return new ResponseEntity<>("잘못된 접근이거나, 필수정보가 누락되었습니다.",HttpStatus.valueOf(400));

        return new ResponseEntity<>(updateGuideProposalDto ,HttpStatus.OK);
    }

    @PatchMapping("/acceptance")
    public ResponseEntity<Object> acceptanceGuideProposal(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideProposalDto acceptGuideProposalDto = guideProposalService.acceptGuideProposal(customMember.getTravelUserId(), guideProposalDto);

        if (acceptGuideProposalDto == null)
            return new ResponseEntity<>("잘못된 접근이거나, 해당 계획은 이미 상대방이 삭제하였습니다." ,HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(acceptGuideProposalDto, HttpStatus.OK);
    }

    @PatchMapping("/refusal")
    public ResponseEntity<Object> refuseGuideProposal(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        GuideProposalDto refuseGuideProposalDto = guideProposalService.refuseGuideProposal(customMember.getTravelUserId(), guideProposalDto);

        if (refuseGuideProposalDto == null)
            return new ResponseEntity<>("잘못된 접근이거나, 해당 계획은 이미 상대방이 삭제하였습니다." ,HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(refuseGuideProposalDto, HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteGuideProposal(@PathVariable Long id, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        String message = guideProposalService.deleteGuideProposal(customMember.getTravelUserId(), id);

        if (message == null)
            return new ResponseEntity<>("삭제된 정보거나, 삭제할 수 없는 상태입니다.", HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
