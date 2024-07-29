package com.ll.travelmate.jwt;

import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Getter
public class JwtUtil {
    @Value("${jwt.secret}") // 바꿀꺼면 나중에 security config 메모리 누수 부분도 확인
    private String secretKey;
    private SecretKey key;


    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtDto convertToDto(String jwtToken, String jwtRefreshToken) {
        JwtDto jwtDto = new JwtDto();

        jwtDto.setJwtToken(jwtToken);
        if (jwtRefreshToken != null) jwtDto.setJwtRefreshToken(jwtRefreshToken);

        return jwtDto;
    }

    public String createToken(Authentication auth, HttpServletResponse response) {
        System.out.println(auth.toString());
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        String authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority()).collect(Collectors.joining(","));

        String jwt = Jwts.builder()
          .claim("MemberId", customMember.getId().toString()) // id 쓸때 toString으로 변환해주기 아니면 소수점으로 나오는것 같음
          .claim("Email", customMember.getUsername())
          .claim("TravelUserId", customMember.getTravelUserId().toString())
          .claim("authorities", authorities)
          .issuedAt(new Date(System.currentTimeMillis()))
          .expiration(new Date(System.currentTimeMillis() + JwtSettingUtil.VALIDITY))
          .signWith(this.key)
          .compact();

        ResponseCookie cookie = ResponseCookie.from(JwtSettingUtil.JWTTOKENNAME, jwt)
            .maxAge(JwtSettingUtil.COOKIEMAXAGE)
            .secure(true)
            .httpOnly(false)
            .path("/")
            .sameSite("None")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

//        Cookie cookie = new Cookie(JwtSettingUtil.JWTTOKENNAME, jwt);
//        cookie.setMaxAge(JwtSettingUtil.COOKIEMAXAGE);
//        cookie.setSecure(true);
//        cookie.setHttpOnly(false);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//
//        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
//        cookie.getName(), cookie.getValue(), cookie.getMaxAge())); // SameSite 속성 공부하기

        System.out.println("createJwtToken func : " + jwt);

        return jwt;
    }

    public String createRefreshToken(Authentication auth, HttpServletResponse response) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        String authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority()).collect(Collectors.joining(","));

        String jwtRefresh = Jwts.builder()
          .claim("MemberId", customMember.getId().toString())
          .claim("authorities", authorities)
          .issuedAt(new Date(System.currentTimeMillis()))
          .expiration(new Date(System.currentTimeMillis() + JwtSettingUtil.REFRESH_VALIDITY))
          .signWith(this.key)
          .compact();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(JwtSettingUtil.JWTREFRESHTOKENNAME, jwtRefresh)
            .maxAge(JwtSettingUtil.REFRESHCOOKIEMAXAGE)
            .secure(true)
            .httpOnly(false)
            .path("/")
            .sameSite("None")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

//        Cookie refreshTokenCookie = new Cookie(JwtSettingUtil.JWTREFRESHTOKENNAME, jwtRefresh);
//        refreshTokenCookie.setMaxAge(JwtSettingUtil.REFRESHCOOKIEMAXAGE);
//        refreshTokenCookie.setSecure(true);
//        refreshTokenCookie.setHttpOnly(false);
//        refreshTokenCookie.setPath("/");
//        response.addCookie(refreshTokenCookie);
//
//        response.addHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
//        refreshTokenCookie.getName(), refreshTokenCookie.getValue(), refreshTokenCookie.getMaxAge())); // SameSite 속성 공부하기

        System.out.println("createJwtRefreshToken func : " + jwtRefresh);

        return jwtRefresh;
    }
    public Claims extractToken(String token) {
        // 변조하면 서명이 일치하지 않게 되므로, 검증시 오류남
        Claims claims = Jwts.parser().verifyWith(this.key).build()
            .parseSignedClaims(token).getPayload();

        System.out.println("extractToken " + claims.toString());
        return claims;
    }

    public void findJwtCookie(Cookie[] cookies, String[] tokenCookies) {
         for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(JwtSettingUtil.JWTTOKENNAME)) {
                tokenCookies[0] = cookies[i].getValue();
                System.out.println("find jwtToken");
            } else if (cookies[i].getName().equals(JwtSettingUtil.JWTREFRESHTOKENNAME)) {
                tokenCookies[1] = cookies[i].getValue();
                System.out.println("find jwtRefreshToken");
            }
        }
    }

    public Authentication setAuthentication(Claims claim, HttpServletRequest request, Member member) {
        String[] arr = claim.get("authorities").toString().split(",");
        var authorities = Arrays.stream(arr).map(a -> new SimpleGrantedAuthority(a)).toList();

        CustomMember customMember;
        if (member != null) {
            customMember = new CustomMember(
                member.getEmail(),
                "none",
                authorities
            );

            customMember.setId(member.getMemberId());
            customMember.setTravelUserId(member.getTravelUser().getTravelUserId());
        } else {
            customMember = new CustomMember(
                claim.get("Email").toString(),
                "none",
                authorities
            );

            customMember.setId(Long.parseLong(claim.get("MemberId").toString()));
            customMember.setTravelUserId(Long.parseLong(claim.get("TravelUserId").toString()));
        }

        var authToken = new UsernamePasswordAuthenticationToken(
            customMember,
            "",
                authorities
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return SecurityContextHolder.getContext().getAuthentication();
    }
}