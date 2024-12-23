package com.ll.travelmate.user;

import com.ll.travelmate.cart.Cart;
import com.ll.travelmate.cart.CartRepository;
import com.ll.travelmate.friend.Friend;
import com.ll.travelmate.friend.FriendRepository;
import com.ll.travelmate.friend.FriendStatus;
import com.ll.travelmate.guide.Guide;
import com.ll.travelmate.guide.GuideRepository;
import com.ll.travelmate.member.CustomMember;
import com.ll.travelmate.member.Member;
import com.ll.travelmate.member.MemberDto;
import com.ll.travelmate.member.MemberRepository;
import com.ll.travelmate.proposal.Proposal;
import com.ll.travelmate.proposal.ProposalRepository;
import com.ll.travelmate.proposal.ProposalStatus;
import com.ll.travelmate.user.externalapi.CompatibilityDto;
import com.ll.travelmate.user.externalapi.RecommendedUsersDto;
import com.ll.travelmate.util.UrlUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TravelUserService {
    private final TravelUserRepository travelUserRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final GuideRepository guideRepository;
    private final CartRepository cartRepository;
    private final ProposalRepository proposalRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    public TravelUserDto convertToDto(TravelUser travelUser, Member member) {
        TravelUserDto dto = new TravelUserDto();
        MemberDto memberDto = new MemberDto();
        //List<FriendDto> friendDtos = new ArrayList<>();

        dto.setTravelUserId(travelUser.getTravelUserId());
        dto.setName(travelUser.getName());
        dto.setGender(travelUser.getGender());
        dto.setAge(travelUser.getAge());
        dto.setAddress(travelUser.getAddress());
        dto.setPhoneNumber(travelUser.getPhoneNumber());
        dto.setImageUrl(travelUser.getImageUrl());
        dto.setIntroduction(travelUser.getIntroduction());
        dto.setCreatedAt(travelUser.getCreatedAt());

        // Member 맵핑
        if (member != null) {
            memberDto.setMemberId(member.getMemberId());
            memberDto.setEmail(member.getEmail());
            memberDto.setPassword("[PRIVATE]");
        }

        // Friend 맵핑
//        for (Friend friend : friends) {
//            FriendDto friendDto = new FriendDto();
//            friendDto.setFriendId(friend.getFriendId());
//            friendDto.setTravelUserId(friend.getTravelUser().getTravelUserId());
//            friendDto.setFriendTravelUser(friend.getFriendTravelUser());
//        }

        // TravelUser 외래키 맵핑
        dto.setMember(memberDto);
        //dto.setFriends(friendDtos);

        System.out.println(memberDto.toString());
        System.out.println(dto.toString());

        return dto;
    }

    @Transactional
    public TravelUserDto createUserAndMember(TravelUserDto travelUserDto, MemberDto memberDto) {
        TravelUser travelUser = new TravelUser();
        Member member = new Member();
        Guide guide = new Guide();
        Cart cart = new Cart();

        travelUser.setName(travelUserDto.getName());
        travelUser.setGender(travelUserDto.getGender());
        travelUser.setAge(travelUserDto.getAge());
        travelUser.setAddress(travelUserDto.getAddress());
        travelUser.setPhoneNumber(travelUserDto.getPhoneNumber());
        travelUser.setImageUrl(travelUserDto.getImageUrl());
        travelUser.setIntroduction(travelUserDto.getIntroduction());

        // Member 생성
        member.setEmail(memberDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        member.setTravelUser(travelUser);

        // Guide 생성
        guide.setAge(travelUserDto.getAge());
        guide.setName(travelUserDto.getName());
        guide.setGender(travelUserDto.getGender());
        guide.setArea(travelUserDto.getAddress());
        guide.setRating(0.0);
        guide.setTotalValue(0.0);
        guide.setReviewCounter(0);
        guide.setTravelUser(travelUser);

        // Cart 생성
        cart.setTravelUser(travelUser);

        // TravelUser와 연결
        travelUser.setMember(member);
        travelUser.setGuide(guide);
        travelUser.setCart(cart);

        // DB 저장
        travelUserRepository.save(travelUser);
        memberRepository.save(member);
        guideRepository.save(guide);
        cartRepository.save(cart);

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
    public TravelUserDto readOtherTravelUser(Long id) {
        Optional<TravelUser> optionalTravelUser = travelUserRepository.findById(id);

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

    public CompatibilityDto findAddCompatibilityRecommendedTravelUsers(String uri, String keyName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        System.out.println("test1");

        ResponseEntity<CompatibilityDto> response = restTemplate.exchange(
                UrlUtil.OTHERSERVERURL + uri,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        System.out.println("test2");

        CompatibilityDto compatibilityDto = response.getBody();
        Long myTravelUserId = Objects.requireNonNull(compatibilityDto).getSelf().getTravel_user_id();
        List<RecommendedUsersDto> recommendedUsersDtos = Objects.requireNonNull(compatibilityDto).getList();
        Set<Long> compatibilityIds = new HashSet<>();

        for (RecommendedUsersDto recommendedUsersDto : recommendedUsersDtos) { // id 추출
            compatibilityIds.add(recommendedUsersDto.getTravel_user_id());
        }

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(myTravelUserId);

        if (travelUserOptional.isEmpty())
            return null; // 잘못된 유저정보

        List<Friend> acceptFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.acceptance);
        List<Friend> refuseFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.refuse);
        List<Friend> blockFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.block);
        List<Friend> friends = new ArrayList<>();

        friends.addAll(acceptFriends);
        friends.addAll(refuseFriends);
        friends.addAll(blockFriends);

        System.out.println(friends.size());

        Set<Long> friendIds = new HashSet<>();

        for (Friend friend : friends) { // 친구 id 추출
            friendIds.add(friend.getFriendTravelUser().getTravelUserId());
        }

        System.out.println("fri"+friendIds.toString());

        Set<Long> difference = new HashSet<>(compatibilityIds);
        difference.removeAll(friendIds); // 친구 제외

        System.out.println("diff"+difference.toString());

        CompatibilityDto newCompatibilityDto = new CompatibilityDto();
        newCompatibilityDto.setSelf(compatibilityDto.getSelf());
        newCompatibilityDto.setList(new ArrayList<>());

        for (RecommendedUsersDto recommendedUsersDto : recommendedUsersDtos) { // id 추출
            if (difference.contains(recommendedUsersDto.getTravel_user_id()))
                newCompatibilityDto.getList().add(recommendedUsersDto);
        }

        return newCompatibilityDto;
    }

    public List<TravelUserDto> findRecommendedTravelUsers(String uri, String keyName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        System.out.println(uri);
        ResponseEntity<Map<String, List<Long>>> response = restTemplate.exchange(
                UrlUtil.OTHERSERVERURL + uri,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        System.out.println(response.getBody().toString());

        // 응답 본문 추출
        Map<String,  List<Long>> responseBody = response.getBody();

        return getTravelUsersByIds(responseBody.get(keyName));
    }

    @Transactional
    public TravelUserDto updateTravelUser(Long travelUserId, TravelUserDto travelUserDto) {
        Optional<TravelUser> optionalTravelUser = travelUserRepository.findById(travelUserId);
        if (optionalTravelUser.isEmpty())
            return null;

        TravelUser travelUser = optionalTravelUser.get();
        Optional<Guide> optionalGuide = guideRepository.findByTravelUser(travelUser);

        if (travelUserDto.getName() != null) {
            travelUser.setName(travelUserDto.getName());
            optionalGuide.get().setName(travelUserDto.getName());
        }
        if (travelUserDto.getGender() != null) {
            travelUser.setGender(travelUserDto.getGender());
            optionalGuide.get().setGender(travelUserDto.getGender());
        }
        if (travelUserDto.getAge() != null) {
            travelUser.setAge(travelUserDto.getAge());
            optionalGuide.get().setAge(travelUserDto.getAge());
        }
        if (travelUserDto.getAddress() != null) {
            travelUser.setAddress(travelUserDto.getAddress());
            optionalGuide.get().setArea(travelUserDto.getAddress());
        }
        if (travelUserDto.getPhoneNumber() != null)
            travelUser.setPhoneNumber(travelUserDto.getPhoneNumber());
        if (travelUserDto.getImageUrl() != null)
            travelUser.setImageUrl(travelUserDto.getImageUrl());
        if (travelUserDto.getIntroduction() != null)
            travelUser.setIntroduction(travelUserDto.getIntroduction());

        travelUserRepository.save(travelUser);
        guideRepository.save(optionalGuide.get());

        return convertToDto(travelUser, travelUser.getMember());
    }

    @Transactional
    public void deleteTravelUser(Long travelUserId) {
        Friend recycleFriend = new Friend();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        List<Friend> requestFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.request);
        List<Friend> acceptanceFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.acceptance);
        List<Friend> refuseFriends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), FriendStatus.refuse);
        List<Proposal> offerProposals = proposalRepository.findByTravelUserAndProposalStatus(travelUserOptional.get(), ProposalStatus.offer);
        List<Proposal> acceptProposals = proposalRepository.findByTravelUserAndProposalStatus(travelUserOptional.get(), ProposalStatus.acceptance);


        for (Friend friend : requestFriends) {
            Optional<Friend> standByFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                    friend.getFriendTravelUser(),
                    friend.getTravelUser(),
                    FriendStatus.standby
            );
            standByFriendOptional.ifPresent(friendRepository::delete);
        }

        for (Friend friend : acceptanceFriends) {
            Optional<Friend> acceptanceFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                    friend.getFriendTravelUser(),
                    friend.getTravelUser(),
                    FriendStatus.acceptance
            );
            acceptanceFriendOptional.ifPresent(friendRepository::delete);
        }

        for (Friend friend : refuseFriends) {
            Optional<Friend> refuseFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                    friend.getFriendTravelUser(),
                    friend.getTravelUser(),
                    FriendStatus.refuse
            );
            refuseFriendOptional.ifPresent(friendRepository::delete);
        }

        for (Proposal proposal : offerProposals) {
            Optional<Proposal> proposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(
                    proposal.getOfferedTravelUser(),
                    proposal.getTravelUser(),
                    ProposalStatus.offered
            );

            proposalOptional.ifPresent(proposalRepository::delete);
        }

        for (Proposal proposal : acceptProposals) {
            Optional<Proposal> proposalOptional = proposalRepository.findByTravelUserAndOfferedTravelUserAndProposalStatus(
                    proposal.getOfferedTravelUser(),
                    proposal.getTravelUser(),
                    ProposalStatus.acceptance
            );

            proposalOptional.ifPresent(proposalRepository::delete);
        }

        travelUserRepository.deleteById(travelUserId);
    }
}
