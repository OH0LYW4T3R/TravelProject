package com.ll.travelmate.user;

import com.ll.travelmate.member.MemberDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRegistrationRequest {
    private TravelUserDto travelUserDto;
    private MemberDto memberDto;

    public UserRegistrationRequest() {}
}
