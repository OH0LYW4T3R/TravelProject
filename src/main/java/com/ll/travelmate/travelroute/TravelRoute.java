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
    @OneToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;
    private String vertex1; private String vertex2; private String vertex3; private String vertex4; private String vertex5;
    private String vertex6; private String vertex7; private String vertex8; private String vertex9; private String vertex10;
    private String vertex11; private String vertex12; private String vertex13; private String vertex14; private String vertex15;
    private String vertex16; private String vertex17; private String vertex18; private String vertex19; private String vertex20;
}
