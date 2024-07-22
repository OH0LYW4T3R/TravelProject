package com.ll.travelmate.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
// 중간 로그인 검증 로직임
public class MyMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> result = memberRepository.findByEmail(username);

        if(result.isEmpty())
            throw new UsernameNotFoundException("아이디가 존재하지 않거나, 비밀번호가 틀렸습니다.");

        Member member = result.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("Normal User"));

        CustomMember customMember = new CustomMember(member.getEmail(), member.getPassword(), authorities);
        customMember.setId(member.getMemberId());
        customMember.setTravelUserId(member.getTravelUser().getTravelUserId());

        // 반환하게 되면 알아서 아이디 비번 맞는지 확인해줌
        return customMember;
    }
}
