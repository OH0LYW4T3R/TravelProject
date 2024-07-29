package com.ll.travelmate.guide;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByTravelUser(TravelUser travelUser);
}
