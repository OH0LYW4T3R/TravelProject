package com.ll.travelmate.proposal;

import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proposal")
public class ProposalController {
    private final ProposalService proposalService;
    @PostMapping("/addition")
    public ResponseEntity<Object> addProposal(@RequestBody ProposalDto proposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        ProposalDto addProposalDto = proposalService.addProposal(customMember.getTravelUserId(), proposalDto);

        if (addProposalDto == null)
            return new ResponseEntity<>("접근할 수 없거나, 이미 제안된 상대입니다.",HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(addProposalDto, HttpStatus.OK);
    }

    @GetMapping("/reading/offer-proposal")
    public ResponseEntity<Object> readOfferProposal(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<ProposalDto> proposalDtos = proposalService.readOfferOrOfferedProposal(customMember.getTravelUserId(), ProposalStatus.offer);
        return new ResponseEntity<>(proposalDtos, HttpStatus.OK);
    }

    @GetMapping("/reading/offered-proposal")
    public ResponseEntity<Object> readOfferedProposal(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<ProposalDto> proposalDtos = proposalService.readOfferOrOfferedProposal(customMember.getTravelUserId(), ProposalStatus.offered);
        return new ResponseEntity<>(proposalDtos, HttpStatus.OK);
    }

    @PatchMapping("/acceptance")
    public ResponseEntity<Object> acceptProposal(@RequestBody ProposalDto proposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        ProposalDto acceptProposalDto = proposalService.acceptProposal(customMember.getTravelUserId(), proposalDto);

        if (acceptProposalDto == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(acceptProposalDto, HttpStatus.OK);
    }

    @PatchMapping("/refusal")
    public ResponseEntity<Object> refuseProposal(@RequestBody ProposalDto proposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
