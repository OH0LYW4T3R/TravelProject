package com.ll.travelmate.proposal;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByTravelUserAndProposalStatus(TravelUser travelUser, ProposalStatus proposalStatus);
    Optional<Proposal> findByTravelUserAndOfferedTravelUserAndProposalStatus(TravelUser travelUser, TravelUser offeredTraveluser, ProposalStatus proposalStatus);
}
