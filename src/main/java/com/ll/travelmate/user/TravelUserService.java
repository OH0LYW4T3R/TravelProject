package com.ll.travelmate.user;

import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.member.Member;
import com.ll.travelmate.member.MemberDto;
import com.ll.travelmate.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public TravelUserDto readTravelUser(CustomMember customMember) {
        if (customMember == null)
            return null;

        Optional<TravelUser> optionalTravelUser = travelUserRepository.findById(customMember.getTravelUserId());

        return optionalTravelUser.map(travelUser -> convertToDto(travelUser, travelUser.getMember())).orElse(null);
    }

    @Transactional
    public List<TravelUserDto> getTravelUsersByIds(List<Long> travelUserIds) {
        List<TravelUserDto> travelUserDtos = new ArrayList<>();
        List<TravelUser> travelUsers = travelUserRepository.findByTravelUserIdIn(travelUserIds);

        for (TravelUser travelUser : travelUsers) {
            travelUserDtos.add(convertToDto(travelUser, travelUser.getMember()));
        }

        return travelUserDtos;
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

        dto.setMember(memberDto);

        System.out.println(memberDto.toString());
        System.out.println(dto.toString());

        return dto;
    }


}
