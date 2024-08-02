package com.ll.travelmate.user.externalapi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecommendedUsersDto {
    private Long travel_user_id;
    private String tendency;
    private Integer ei;
    private Integer sn;
    private Integer ft;
    private Integer pj;
}
