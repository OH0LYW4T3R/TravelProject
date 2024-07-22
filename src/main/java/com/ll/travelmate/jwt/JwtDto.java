package com.ll.travelmate.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JwtDto {
    @NotNull(message = "JwtToken is required")
    private String jwtToken;
    private String jwtRefreshToken;
}
