package com.ll.travelmate.friend;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByTravelUserAndFriendTravelUserAndFriendStatus(TravelUser travelUser, TravelUser friendTravelUser, FriendStatus friendStatus);
    List<Friend> findByTravelUserAndFriendStatus(TravelUser travelUser, FriendStatus friendStatus);
}
