package com.ll.travelmate.user;

import com.ll.travelmate.block.Block;
import com.ll.travelmate.cart.Cart;
import com.ll.travelmate.friend.Friend;
import com.ll.travelmate.guide.Guide;
import com.ll.travelmate.member.Member;
import com.ll.travelmate.proposal.Proposal;
import com.ll.travelmate.travel.Travel;
import com.ll.travelmate.travelsetting.TravelSetting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class TravelUser {
    // 관계에서는 양방향 단방향을 만들 수 있으므로, 잊을시 추가 할 것
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelUserId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer age;
    private String address;
    private String phoneNumber;
    private String imageUrl;
    private String introduction;
    @CreationTimestamp
    private LocalDateTime createdAt;

    // mappedBy 지정시 연관관계 주인에게만 외부키가 보이며 자신의 테이블에서는 보이지 않는다.
    @OneToOne(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // mappedBy에 연관관계의 주인 필드 이름을 넣어주면 된다.
    @ToString.Exclude
    private Member member;
    @OneToOne(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart; // 추가
    @OneToOne(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Guide guide;
    @OneToOne(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TravelSetting travelSetting;

    @OneToMany(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // mappedBy에 연관관계의 주인 필드 이름을 넣어주면 된다.
    private List<Friend> friends = new ArrayList<>();
    @OneToMany(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // mappedBy에 연관관계의 주인 필드 이름을 넣어주면 된다.
    private List<Block> blocks = new ArrayList<>();
    @OneToMany(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Travel> travels = new ArrayList<>();
    @OneToMany(mappedBy = "travelUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Proposal> proposal = new ArrayList<>();
}
