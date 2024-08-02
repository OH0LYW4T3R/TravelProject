package com.ll.travelmate.guide;

import com.ll.travelmate.user.TravelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByTravelUser(TravelUser travelUser);
    @Query(value = "SELECT * FROM Guide WHERE area LIKE %:area%", nativeQuery = true)
    List<Guide> findByAreaContaining(@Param("area") String area);
}
