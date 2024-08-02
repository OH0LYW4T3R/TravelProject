package com.ll.travelmate.proposal;

import com.ll.travelmate.travel.TravelDto;
import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ProposalDto {
    private Long proposalId;
    private Long travelUserId;
    private TravelUserDto travelUser;
    private Long offeredTravelUserId;
    private TravelUserDto offeredTravelUser;
    private TravelDto travel;
    private LocalDateTime refuseTime;
    private LocalDateTime createdAt;
    private ProposalStatus proposalStatus;
}
