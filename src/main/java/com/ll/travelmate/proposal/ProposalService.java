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
import java.util.Objects;
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

        if (!Objects.equals(travelOptional.get().getTravelUser().getTravelUserId(), travelUserId))
            return null; // 내가 만든 여행이 아니면

        Optional<Proposal> proposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(travelUserOptional.get(), offeredTravelUserOptional.get(), ProposalStatus.offer);

        if (proposalOptional.isPresent())
            return null; // 이미 제안함

        System.out.println(travelUserOptional.get().getTravelUserId().toString());

        offerProposal.setTravelUser(travelUserOptional.get());
        offerProposal.setOfferedTravelUser(offeredTravelUserOptional.get());
        offerProposal.setTravel(travelOptional.get());
        offerProposal.setProposalStatus(ProposalStatus.valueOf("offer"));
        proposalRepository.save(offerProposal);

        offeredProposal.setTravelUser(offeredTravelUserOptional.get());
        offeredProposal.setOfferedTravelUser(travelUserOptional.get());
        offeredProposal.setTravel(travelOptional.get());
        offeredProposal.setProposalStatus(ProposalStatus.valueOf("offered"));
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

    @Transactional
    public ProposalDto acceptProposal(Long travelUserId, ProposalDto proposalDto) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> offerTravelUserOptional = travelUserRepository.findById(proposalDto.getOfferedTravelUserId());

        if (offerTravelUserOptional.isEmpty())
            return null; // 제안한 사람 없음

        Optional<Proposal> offeredProposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(
                travelUserOptional.get(),
                offerTravelUserOptional.get(),
                ProposalStatus.offered
        );

        Optional<Proposal> offerProposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(
                offerTravelUserOptional.get(),
                travelUserOptional.get(),
                ProposalStatus.offer
        );

        if (offerProposalOptional.isEmpty() || offeredProposalOptional.isEmpty())
            return null; // 제시된게 없음

        offeredProposalOptional.get().setProposalStatus(ProposalStatus.acceptance);
        offerProposalOptional.get().setProposalStatus(ProposalStatus.acceptance);

        return convertToDto(offeredProposalOptional.get(), null, offeredProposalOptional.get().getOfferedTravelUser());
    }

    public ProposalDto convertToDto(Proposal proposal, TravelUser travelUser, TravelUser offeredTravelUser) {
        ProposalDto proposalDto = new ProposalDto();

        proposalDto.setProposalId(proposal.getProposalId());
        proposalDto.setTravelUserId(proposal.getTravelUser().getTravelUserId());
        proposalDto.setOfferedTravelUserId(proposal.getOfferedTravelUser().getTravelUserId());
        proposalDto.setTravel(travelService.convertToDto(proposal.getTravel()));
        proposalDto.setCreatedAt(proposal.getCreatedAt());
        proposalDto.setProposalStatus(proposal.getProposalStatus());

        if (proposal.getRefuseTime() != null)
            proposalDto.setRefuseTime(proposal.getRefuseTime());

        if (travelUser != null)
            proposalDto.setTravelUser(travelUserService.convertToDto(travelUser, null));

        if (offeredTravelUser != null)
            proposalDto.setOfferedTravelUser(travelUserService.convertToDto(offeredTravelUser, null));

        return proposalDto;
    }
}
