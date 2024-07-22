package com.ll.travelmate.member;

import com.ll.travelmate.jwt.JwtDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Object> loginJWT(@RequestBody MemberDto memberDto, HttpServletResponse response, HttpServletRequest request) {
        System.out.println(request.getHeader("X-XSRF-TOKEN"));
        JwtDto jwtDto = memberService.jwtCreate(memberDto, response);
        return new ResponseEntity<>(jwtDto, HttpStatus.OK);
    }

    @GetMapping("/test")
    @Transactional
    public ResponseEntity<Object> jwtTest(Authentication auth) {
        if (auth == null) {
            System.out.println("null 임");
            return new ResponseEntity<>("jwt fail", HttpStatus.NOT_FOUND);
        }
        else
            System.out.println("null 아님");
        CustomMember user = (CustomMember) auth.getPrincipal();
        System.out.println(user.toString());
        System.out.println(user.getId());
        System.out.println(user.getTravelUserId());
        System.out.println(user.getUsername());

        return new ResponseEntity<>("jwt open", HttpStatus.OK);
    }
}
