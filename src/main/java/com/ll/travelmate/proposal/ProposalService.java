package com.ll.travelmate.proposal;

import com.ll.travelmate.travel.Travel;
import com.ll.travelmate.travel.TravelRepository;
import com.ll.travelmate.travel.TravelService;
import com.ll.travelmate.travelroute.TravelRoute;
import com.ll.travelmate.travelroute.TravelRouteService;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import com.ll.travelmate.util.ProductRefreshUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
*
* 제안 거절시 3일동안은 다시 제안 하지 못한다.
*
*
*
* */

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final TravelUserRepository travelUserRepository;
    private final TravelRepository travelRepository;
    private final ProposalRepository proposalRepository;
    private final TravelUserService travelUserService;
    private final TravelService travelService;
    private final TravelRouteService travelRouteService;

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

        if (ProductRefreshUtil.startTimeCheck(travelOptional.get().getTravelStartDate()))
            return null; // 이미 시작날짜를 넘으면

        Optional<Proposal> proposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(travelUserOptional.get(), offeredTravelUserOptional.get(), ProposalStatus.offer);

        if (proposalOptional.isPresent())
            return null; // 이미 제안함

        Optional<Proposal> refusalProposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(travelUserOptional.get(), offeredTravelUserOptional.get(), ProposalStatus.refusal);

        if (refusalProposalOptional.isPresent()) {
            if (refusalTimeCheck(refusalProposalOptional.get()))
                return null; // 다시 제안할 시간이 안지남.
        }

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
            boolean check = refusalTimeCheck(proposal);
            boolean check2 = startTimeCheck(proposal);

            if (check && check2)
                proposalDtos.add(convertToDto(proposal, null, proposal.getOfferedTravelUser()));
        }

        return proposalDtos;
    }

    @Transactional
    public ProposalDto readDetailProposal(Long proposalId) {
        Optional<Proposal> proposalOptional = proposalRepository.findById(proposalId);
        return proposalOptional.map(proposal -> convertToDto(proposal, null, proposal.getOfferedTravelUser())).orElse(null);
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

        //수락시 내쪽에 Travel 추가
        Travel offeredTravel = new Travel();
//        offeredTravel.setTravelId(null);
        offeredTravel.setTravelUser(offeredProposalOptional.get().getTravelUser());
        offeredTravel.setMateUser(offeredProposalOptional.get().getOfferedTravelUser());
        offeredTravel.setLocation(offeredProposalOptional.get().getTravel().getLocation());
        offeredTravel.setLongitude(offeredProposalOptional.get().getTravel().getLongitude());
        offeredTravel.setLatitude(offeredProposalOptional.get().getTravel().getLatitude());
        offeredTravel.setTravelStartDate(offeredProposalOptional.get().getTravel().getTravelStartDate());
        offeredTravel.setTravelEndDate(offeredProposalOptional.get().getTravel().getTravelEndDate());
        for (TravelRoute travelRoute : offeredProposalOptional.get().getTravel().getTravelRoute()) {
            offeredTravel.getTravelRoute().add(travelRouteService.addTravelRoute(offeredTravel, TravelRouteService.convertToDto(travelRoute)));
        }
        travelRepository.save(offeredTravel);
        //상대방은 Travel에 mate 추가
        Travel offerTravel = offerProposalOptional.get().getTravel();
        offerTravel.setMateUser(offerProposalOptional.get().getOfferedTravelUser());
        travelRepository.save(offerTravel);

        return convertToDto(offeredProposalOptional.get(), null, offeredProposalOptional.get().getOfferedTravelUser());
    }

    @Transactional
    public void refuseProposal(Long travelUserId, ProposalDto proposalDto) {
        //내쪽에선 삭제, 상대쪽에선 거절 했다는 표시가 뜸
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> offerTravelUserOptional = travelUserRepository.findById(proposalDto.getOfferedTravelUserId());

        if (offerTravelUserOptional.isEmpty())
            return; // 제안한 사람 없음

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
            return; // 제시된게 없음

        proposalRepository.delete(offeredProposalOptional.get());

        offerProposalOptional.get().setProposalStatus(ProposalStatus.refusal);
        offerProposalOptional.get().setRefuseTime(LocalDateTime.now());

        proposalRepository.save(offerProposalOptional.get());
    }

    @Transactional
    public void deleteProposal(Long travelUserId, ProposalDto proposalDto) {
        Optional<Proposal> proposalOptional = proposalRepository.findById(proposalDto.getProposalId());

        if (proposalOptional.isEmpty())
            return;

        if (!Objects.equals(proposalOptional.get().getTravelUser().getTravelUserId(), travelUserId))
            return;

        if (proposalOptional.get().getProposalStatus() != ProposalStatus.offer)
            return;

        Optional<Proposal> offeredProposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(
                proposalOptional.get().getOfferedTravelUser(),
                proposalOptional.get().getTravelUser(),
                ProposalStatus.offered
        );

        offeredProposalOptional.ifPresent(proposalRepository::delete);
        proposalRepository.delete(proposalOptional.get());
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

    public boolean startTimeCheck(Proposal proposal) {
        if (proposal.getProposalStatus() == ProposalStatus.offer || proposal.getProposalStatus() == ProposalStatus.offered) {
                if (ProductRefreshUtil.startTimeCheck(proposal.getTravel().getTravelStartDate())) {
                    proposalRepository.delete(proposal);
                    return false;
                }
        }

        return true;
    }

    public boolean refusalTimeCheck(Proposal proposal) {
        if (proposal.getProposalStatus() == ProposalStatus.refusal) {
                if (ProductRefreshUtil.isPaymentPassed(proposal.getRefuseTime())) {
                    proposalRepository.delete(proposal);
                    return false;
                }
        }

        return true;
    }
}
