package com.ll.travelmate.travel;

import com.ll.travelmate.travelroute.TravelRoute;
import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Travel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;
    private String location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="travel_user_id")
    private TravelUser travelUser;
    @OneToOne // 단방향
    @JoinColumn(name = "mate_id")
    private TravelUser mateUser;
    @OneToOne(mappedBy = "travel")
    private TravelRoute travelRoute;
}
