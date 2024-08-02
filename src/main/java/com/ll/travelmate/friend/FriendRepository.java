package com.ll.travelmate.friend;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByTravelUserAndFriendTravelUserAndFriendStatus(TravelUser travelUser, TravelUser friendTravelUser, FriendStatus friendStatus);
    List<Friend> findByTravelUserAndFriendStatus(TravelUser travelUser, FriendStatus friendStatus);
}
