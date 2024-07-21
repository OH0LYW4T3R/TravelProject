package com.ll.travelmate.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long memberId;
    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    private String password;
}