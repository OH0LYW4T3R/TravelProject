package com.ll.travelmate.proposal;

import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.travel.TravelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
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
            return new ResponseEntity<>(HttpStatus.OK);

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
}
