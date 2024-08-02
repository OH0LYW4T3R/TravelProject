package com.ll.travelmate.travelroute;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TravelRouteDto {
    private Long travelRouteId;
    private Long travelId; // Travel의 ID
    private String vertex;
    private Double latitude;
    private Double longitude;
}
