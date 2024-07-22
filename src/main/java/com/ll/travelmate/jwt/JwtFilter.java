package com.ll.travelmate.jwt;

import com.ll.travelmate.member.Member;
import com.ll.travelmate.member.MemberRepository;
import com.ll.travelmate.user.TravelUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TravelUserRepository travelUserRepository;
    private final MemberRepository memberRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] tokenCookies = new String[2];
        jwtUtil.findJwtCookie(cookies, tokenCookies);
        String jwtCookie = tokenCookies[0];
        String jwtRefreshCookie = tokenCookies[1];

        Claims claim;
        try {
            claim = jwtUtil.extractToken(jwtCookie);
        } catch (Exception e) {
            System.out.println("엑세스 토큰 유효기간 만료 혹은 유효하지 않음");

            try {
                Claims refreshClaim = jwtUtil.extractToken(jwtRefreshCookie);

                Optional<Member> optionalMember =  memberRepository.findById(Long.parseLong(refreshClaim.get("MemberId").toString()));

                System.out.printf("리프레시 토큰으로 처리중");
                String newJwt = jwtUtil.createToken(jwtUtil.setAuthentication(refreshClaim, request, optionalMember.get()), response);
                System.out.printf("리프레시 토큰으로 처리완료");

                claim = jwtUtil.extractToken(newJwt);
            } catch (Exception ex) {
                System.out.println("리프레시 토큰이 만료되었거나, 유효하지 않음.");
                filterChain.doFilter(request, response);
                return;
            }
        }

        jwtUtil.setAuthentication(claim, request, null);

        filterChain.doFilter(request, response);
    }
}
