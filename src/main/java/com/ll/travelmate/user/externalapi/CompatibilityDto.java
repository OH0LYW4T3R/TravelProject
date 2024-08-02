package com.ll.travelmate.user.externalapi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CompatibilityDto {
    private List<RecommendedUsersDto> list;
    private RecommendedUsersDto self;
}
