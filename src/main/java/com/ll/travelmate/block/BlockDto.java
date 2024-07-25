package com.ll.travelmate.block;

import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BlockDto {
    private Long travelUserId;
    private Long blockedTravelUserId;
    private TravelUserDto blockedTravelUserDto;
}
