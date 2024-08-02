package com.ll.travelmate.travelroute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelRouteRepository extends JpaRepository<TravelRoute, Long> {
}
