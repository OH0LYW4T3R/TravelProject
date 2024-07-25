package com.ll.travelmate.friend;

import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Friend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_user_id") // 외래키를 가지고 있는 객체가 연관관계의 주인이다.
    private TravelUser travelUser;

    @OneToOne // 단방향
    @JoinColumn(name = "freind_travel_user_id", unique = false)
    private TravelUser friendTravelUser;

    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus;

    // 친구 상태 (수락, 거절, 대기, 요청) 등등을 표시할 것이 필요하고, 자신의 아이디에도 저장, 상대방 아이디에도 저장해야함 (총 두번)
    // 내가 친구 추가를 하면 내쪽 상태에는 대기, 상대방쪽 상태에는 요청
    // 로그인 한 사람 기준 요청을 보여주게 되면 -> 친구 요청을 받은 것을 볼 수 있다.
    // 로그인 한 사람 기준 대기를 보여주게 되면 -> 친구 요청을 한 것을 볼 수 있다.
    // 로그인 한 사람 기준 수락을 보여주게 되면 -> 친추가 된 회원들을 볼 수 있다.
    // 로그인 한 사람 기준 거절을 누르면 -> 로그인 한 사람을 기준으로 Friend를 삭제한다
    // 로그인 한 사람 기준 수락을 누르면 -> 서로의 친구 상태를 수락으로 업데이트 한다.
    // 로그인 한 사람 기준 친추를 누르면 -> 로그인 한 사람은 "대기", 상대방은 "요청"을 표시한다.
}
