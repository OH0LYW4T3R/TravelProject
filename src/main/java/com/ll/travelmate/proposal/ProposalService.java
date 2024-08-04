package com.ll.travelmate.proposal;

import com.ll.travelmate.travel.Travel;
import com.ll.travelmate.travel.TravelRepository;
import com.ll.travelmate.travel.TravelService;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final TravelUserRepository travelUserRepository;
    private final TravelRepository travelRepository;
    private final ProposalRepository proposalRepository;
    private final TravelUserService travelUserService;
    private final TravelService travelService;

    @Transactional
    public ProposalDto addProposal(Long travelUserId, ProposalDto proposalDto) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> offeredTravelUserOptional = travelUserRepository.findById(proposalDto.getOfferedTravelUserId());
        Optional<Travel> travelOptional = travelRepository.findById(proposalDto.getTravel().getTravelId());
        Proposal offerProposal = new Proposal();
        Proposal offeredProposal = new Proposal();

        if (offeredTravelUserOptional.isEmpty())
            return null; // 제안할 사용자가 없음.

        if (travelOptional.isEmpty())
            return null;

        offerProposal.setTravelUser(travelUserOptional.get());
        offerProposal.setOfferedTravelUser(offeredTravelUserOptional.get());
        offerProposal.setTravel(travelOptional.get());
        offerProposal.setProposalStatus(ProposalStatus.offer);

        offeredProposal.setTravelUser(offeredTravelUserOptional.get());
        offeredProposal.setOfferedTravelUser(travelUserOptional.get());
        offeredProposal.setTravel(travelOptional.get());
        offerProposal.setProposalStatus(ProposalStatus.offered);

        proposalRepository.save(offerProposal);
        proposalRepository.save(offeredProposal);

        return convertToDto(offerProposal, null, offerProposal.getOfferedTravelUser());
    }

    @Transactional
    public List<ProposalDto> readOfferOrOfferedProposal(Long travelUserId, ProposalStatus proposalStatus) {
        List<ProposalDto> proposalDtos = new ArrayList<>();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        List<Proposal> proposals = proposalRepository.findByTravelUserAndProposalStatus(travelUserOptional.get(), proposalStatus);

        for (Proposal proposal : proposals) {
            proposalDtos.add(convertToDto(proposal, null, proposal.getOfferedTravelUser()));
        }

        return proposalDtos;
    }
    public ProposalDto convertToDto(Proposal proposal, TravelUser travelUser, TravelUser offeredTravelUser) {
        ProposalDto proposalDto = new ProposalDto();

        proposalDto.setProposalId(proposalDto.getProposalId());
        proposalDto.setTravelUserId(proposal.getTravelUser().getTravelUserId());
        proposalDto.setOfferedTravelUserId(proposal.getOfferedTravelUser().getTravelUserId());
        proposalDto.setTravel(travelService.convertToDto(proposal.getTravel()));
        proposalDto.setCreatedAt(proposal.getCreatedAt());
        proposalDto.setProposalStatus(proposalDto.getProposalStatus());

        if (proposal.getRefuseTime() != null)
            proposalDto.setRefuseTime(proposal.getRefuseTime());

        if (travelUser != null)
            proposalDto.setTravelUser(travelUserService.convertToDto(travelUser, null));

        if (offeredTravelUser != null)
            proposalDto.setOfferedTravelUser(travelUserService.convertToDto(offeredTravelUser, null));

        return proposalDto;
    }
}
