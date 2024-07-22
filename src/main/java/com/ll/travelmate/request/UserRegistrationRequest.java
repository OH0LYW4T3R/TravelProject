package com.ll.travelmate.request;

import com.ll.travelmate.member.MemberDto;
import com.ll.travelmate.user.TravelUserDto;
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
