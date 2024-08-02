package com.ll.travelmate.guideproposal;

import com.ll.travelmate.cart.Cart;
import com.ll.travelmate.guide.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideProposalRepository extends JpaRepository<GuideProposal, Long> {
    Optional<GuideProposal> findByGuideAndGuideProposalId(Guide guide, Long guideProposalId);
    List<GuideProposal> findByGuideAndPurchaseStatus(Guide guide, PurchaseStatus purchaseStatus);
    List<GuideProposal> findByCartAndPurchaseStatus(Cart cart, PurchaseStatus purchaseStatus);
    @Query(value = "SELECT * FROM guide_proposal WHERE area LIKE %:area%", nativeQuery = true)
    List<GuideProposal> findByAreaContaining(@Param("area") String area);

}
