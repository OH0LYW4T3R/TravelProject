package com.ll.travelmate.user;

import com.ll.travelmate.member.Member;
import com.ll.travelmate.member.MemberDto;
import com.ll.travelmate.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TravelUserService {
    private final TravelUserRepository travelUserRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TravelUserDto createUserAndMember(TravelUserDto travelUserDto, MemberDto memberDto) {
        TravelUser travelUser = new TravelUser();
        Member member = new Member();

        travelUser.setName(travelUserDto.getName());
        travelUser.setGender(travelUserDto.getGender());
        travelUser.setAge(travelUserDto.getAge());
        travelUser.setAddress(travelUserDto.getAddress());
        travelUser.setPhoneNumber(travelUserDto.getPhoneNumber());
        travelUser.setImageUrl(travelUserDto.getImageUrl());
        travelUser.setIntroduction(travelUserDto.getIntroduction());

        member.setEmail(memberDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        member.setTravelUser(travelUser);

        travelUser.setMember(member);

        travelUserRepository.save(travelUser);
        memberRepository.save(member);

        // 나중에 다른 엔티티를 추가하거나 업데이트할 수 있습니다.
        return convertToDto(travelUser, member);
    }
    private TravelUserDto convertToDto(TravelUser travelUser, Member member) {
        TravelUserDto dto = new TravelUserDto();
        MemberDto memberDto = new MemberDto();

        dto.setTravelUserId(travelUser.getTravelUserId());
        dto.setName(travelUser.getName());
        dto.setGender(travelUser.getGender());
        dto.setAge(travelUser.getAge());
        dto.setAddress(travelUser.getAddress());
        dto.setPhoneNumber(travelUser.getPhoneNumber());
        dto.setImageUrl(travelUser.getImageUrl());
        dto.setIntroduction(travelUser.getIntroduction());
        dto.setCreatedAt(travelUser.getCreatedAt());

        memberDto.setMemberId(member.getMemberId());
        memberDto.setEmail(member.getEmail());
        memberDto.setPassword("[PRIVATE]");

        System.out.println(memberDto.toString());
        System.out.println(dto.toString());

        return dto;
    }
}
