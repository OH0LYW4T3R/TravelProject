package com.ll.travelmate.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
    List<TravelUser> findByTravelUserIdIn(List<Long> ids);
    List<TravelUser> findByTravelUserIdInAndTravelUserIdNotIn(List<Long> includeIds, List<Long> excludeIds);
}
