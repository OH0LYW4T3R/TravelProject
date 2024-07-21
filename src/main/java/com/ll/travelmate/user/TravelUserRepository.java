package com.ll.travelmate.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
}
