package com.ll.travelmate.member;

import com.ll.travelmate.jwt.JwtDto;
import com.ll.travelmate.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;

    public JwtDto jwtCreate(MemberDto memberDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                memberDto.getEmail(), memberDto.getPassword()
        );

        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
        String jwt = jwtUtil.createToken(auth2, response);
        String jwtRefresh = jwtUtil.createRefreshToken(auth2, response);

        return jwtUtil.convertToDto(jwt, jwtRefresh);
    }
}
