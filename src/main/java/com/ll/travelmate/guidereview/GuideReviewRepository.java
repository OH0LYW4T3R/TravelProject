package com.ll.travelmate.guidereview;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideReviewRepository extends JpaRepository<GuideReview, Long> {
    Optional<GuideReview> findByGuideReviewIdAndTravelUser(Long guideReviewId, TravelUser travelUser);
    Optional<GuideReview> findByGuideProposalIdAndTravelUser(Long guideProposalId, TravelUser travelUser);
    List<GuideReview> findByTravelUser(TravelUser travelUser);
}
