package com.ll.travelmate.friend;

import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final TravelUserRepository travelUserRepository;
    private final FriendRepository friendRepository;
    private final TravelUserService travelUserService;

    @Transactional
    public FriendDto addFriend(Long travelUserId, FriendDto friendDto) {
        if (equalTravelUserId(travelUserId, friendDto.getFriendTravelUserId()) == 1)
            return null;

        Friend travelUserSet = new Friend();
        Friend friendTravelUserSet = new Friend();
        Long friendTravelUserId = friendDto.getFriendTravelUserId();

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> friendTravelUserOptional = travelUserRepository.findById(friendTravelUserId);

        if (travelUserOptional.isPresent() && friendTravelUserOptional.isPresent()) {
            travelUserSet.setTravelUser(travelUserOptional.get());
            friendTravelUserSet.setTravelUser(friendTravelUserOptional.get());
        } else
            return null;

        if (friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(travelUserOptional.get(), friendTravelUserOptional.get(), FriendStatus.refuse).isPresent() ||
        friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(friendTravelUserOptional.get(), travelUserOptional.get(), FriendStatus.refuse).isPresent())
            // 내가 거절한 상대거나, 상대방이 거절한 경우 친추가 안됨.
            return null;

        if (friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(travelUserOptional.get(), friendTravelUserOptional.get(), FriendStatus.request).isPresent())
            return null;

        travelUserSet.setFriendTravelUser(friendTravelUserOptional.get());
        travelUserSet.setFriendStatus(FriendStatus.valueOf("request"));
        friendRepository.save(travelUserSet);

        friendTravelUserSet.setFriendTravelUser(travelUserOptional.get());
        friendTravelUserSet.setFriendStatus(FriendStatus.valueOf("standby"));
        friendRepository.save(friendTravelUserSet);

        return convertFriendDto(travelUserOptional.get(), friendTravelUserOptional.get(), FriendStatus.valueOf("request"));
    }

    @Transactional
    public List<FriendDto> readList(Long travelUserId, FriendStatus friendStatus) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);

        if (travelUserOptional.isEmpty())
            return null;

        List<FriendDto> friendDtos = new ArrayList<>();
        List<Friend> friends = friendRepository.findByTravelUserAndFriendStatus(travelUserOptional.get(), friendStatus);

        for (Friend friend : friends) {
            friendDtos.add(convertFriendDto(friend.getTravelUser(), friend.getFriendTravelUser(), friendStatus));
        }

        return friendDtos;
    }

    @Transactional
    public FriendDto acceptFriend(Long travelUserId, FriendDto friendDto) {
        if (equalTravelUserId(travelUserId, friendDto.getFriendTravelUserId()) == 1)
            return null;

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> friendTravelUserOptional = travelUserRepository.findById(friendDto.getFriendTravelUserId());
        if (travelUserOptional.isEmpty() || friendTravelUserOptional.isEmpty()) // 상대방이 탈퇴했을 가능성도 생각.
            return null;

        // 상대방이 먼저 걸어온 경우
        Optional<Friend> userToFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                travelUserOptional.get(),
                friendTravelUserOptional.get(),
                FriendStatus.standby
        );
        Optional<Friend> friendToUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                friendTravelUserOptional.get(),
                travelUserOptional.get(),
                FriendStatus.request
        );

        if (userToFriendOptional.isEmpty() || friendToUserOptional.isEmpty())  // 이미 친추가 된 상태
            return null;

        userToFriendOptional.get().setFriendStatus(FriendStatus.acceptance);
        friendToUserOptional.get().setFriendStatus(FriendStatus.acceptance);

        friendRepository.save(userToFriendOptional.get());
        friendRepository.save(friendToUserOptional.get());

        //만약 내가 친구 요청을 한것도 있으면?
        Optional<Friend> reverseUserToFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                travelUserOptional.get(),
                friendTravelUserOptional.get(),
                FriendStatus.request
        );
        Optional<Friend> reverseFriendToUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                friendTravelUserOptional.get(),
                travelUserOptional.get(),
                FriendStatus.standby
        );

        if (reverseUserToFriendOptional.isPresent() && reverseFriendToUserOptional.isPresent()) {
            friendRepository.delete(reverseUserToFriendOptional.get());
            friendRepository.delete(reverseFriendToUserOptional.get());
        }

        return convertFriendDto(travelUserOptional.get(), friendTravelUserOptional.get(), FriendStatus.acceptance);
    }

    @Transactional
    public FriendDto refuseFriend(Long travelUserId, FriendDto friendDto) {
        if (equalTravelUserId(travelUserId, friendDto.getFriendTravelUserId()) == 1)
            return null;

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> friendTravelUserOptional = travelUserRepository.findById(friendDto.getFriendTravelUserId());
        if (travelUserOptional.isEmpty() || friendTravelUserOptional.isEmpty()) // 상대방이 탈퇴했을 가능성도 생각.
            return null;

        // 상대방이 먼저 걸어온 경우
        Optional<Friend> userToFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                travelUserOptional.get(),
                friendTravelUserOptional.get(),
                FriendStatus.standby
        );
        Optional<Friend> friendToUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                friendTravelUserOptional.get(),
                travelUserOptional.get(),
                FriendStatus.request
        );

        // request

        if (userToFriendOptional.isEmpty() || friendToUserOptional.isEmpty())  // 이미 거절이 된 상태
            return null;

        userToFriendOptional.get().setFriendStatus(FriendStatus.refuse);
        friendRepository.save(userToFriendOptional.get());

        friendToUserOptional.ifPresent(friendRepository::delete);

        //만약 내가 친구 요청을 한것도 있으면?
        Optional<Friend> reverseUserToFriendOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                travelUserOptional.get(),
                friendTravelUserOptional.get(),
                FriendStatus.request
        );
        Optional<Friend> reverseFriendToUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                friendTravelUserOptional.get(),
                travelUserOptional.get(),
                FriendStatus.standby
        );

        if (reverseUserToFriendOptional.isPresent() && reverseFriendToUserOptional.isPresent()) {
            friendRepository.delete(reverseUserToFriendOptional.get());
            friendRepository.delete(reverseFriendToUserOptional.get());
        }

        return convertFriendDto(travelUserOptional.get(), friendTravelUserOptional.get(), FriendStatus.refuse);
    }

    @Transactional
    public void removeFriend(Long travelUserId, FriendDto friendDto) {
        if (equalTravelUserId(travelUserId, friendDto.getFriendTravelUserId()) == 1)
            return;

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<TravelUser> friendTravelUserOptional = travelUserRepository.findById(friendDto.getFriendTravelUserId());
        Optional<Friend> travelUserToFrinedOptional;
        Optional<Friend> friendtoTravelUserOptional;

        if (friendTravelUserOptional.isEmpty())
            return;

        switch (friendDto.getFriendStatus()) {
            case acceptance:
                travelUserToFrinedOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                        travelUserOptional.get(),
                        friendTravelUserOptional.get(),
                        FriendStatus.acceptance
                );
                friendtoTravelUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                        friendTravelUserOptional.get(),
                        travelUserOptional.get(),
                        FriendStatus.acceptance
                );

                travelUserToFrinedOptional.ifPresent(friendRepository::delete);
                friendtoTravelUserOptional.ifPresent(friendRepository::delete);
                break;
            case request:
                travelUserToFrinedOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                        travelUserOptional.get(),
                        friendTravelUserOptional.get(),
                        FriendStatus.request
                );
                friendtoTravelUserOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                        friendTravelUserOptional.get(),
                        travelUserOptional.get(),
                        FriendStatus.standby
                );

                travelUserToFrinedOptional.ifPresent(friendRepository::delete);
                friendtoTravelUserOptional.ifPresent(friendRepository::delete);
                break;
            case refuse:
                travelUserToFrinedOptional = friendRepository.findByTravelUserAndFriendTravelUserAndFriendStatus(
                        travelUserOptional.get(),
                        friendTravelUserOptional.get(),
                        FriendStatus.refuse
                );

                travelUserToFrinedOptional.ifPresent(friendRepository::delete);
                break;
        }
    }

    public FriendDto convertFriendDto(TravelUser travelUser, TravelUser friendTravelUser, FriendStatus friendStatus) {
        FriendDto friendDto = new FriendDto();
        friendDto.setTravelUserId(travelUser.getTravelUserId());
        friendDto.setFriendTravelUserId(friendTravelUser.getTravelUserId());
        friendDto.setFriendTravelUserDto(travelUserService.convertToDto(friendTravelUser, null));
        friendDto.setFriendStatus(friendStatus);

        return friendDto;
    }

    public Integer equalTravelUserId(Long travelUserId, Long friendTravelUserId) {
        if (Objects.equals(travelUserId, friendTravelUserId))
            return 1;
        return -1;
    }
}