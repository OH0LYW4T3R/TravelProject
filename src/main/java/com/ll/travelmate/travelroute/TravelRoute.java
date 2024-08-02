package com.ll.travelmate.travelroute;

import com.ll.travelmate.travel.Travel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TravelRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelRouteId;
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;
    private String vertex;
    private Double latitude;
    private Double longitude;
}
