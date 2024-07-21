package com.ll.travelmate.block;

import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="travel_user_id") // 외래키를 가지고 있는 객체가 연관관계의 주인이다.
    private TravelUser travelUser;

    @OneToOne // 단방향
    @JoinColumn(name = "blocked_travel_user_id")
    private TravelUser blockedTravelUser;
}
