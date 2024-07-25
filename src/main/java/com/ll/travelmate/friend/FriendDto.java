package com.ll.travelmate.friend;

import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FriendDto {
    private Long travelUserId;
    private Long friendTravelUserId;
    private TravelUserDto friendTravelUserDto;
    private FriendStatus friendStatus;
}
