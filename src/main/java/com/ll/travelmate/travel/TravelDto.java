package com.ll.travelmate.travel;

import com.ll.travelmate.travelroute.TravelRouteDto;
import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class TravelDto {
    private Long travelId;
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long travelUserId; // travelUser의 ID
    private TravelUserDto mateUserDto; // mateUser의 ID
    private List<TravelRouteDto> travelRoute; // TravelRoute의 DTO 리스트
}
