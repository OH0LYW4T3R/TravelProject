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
    @JoinColumn(name = "freind_travel_user_id")
    private TravelUser friendTravelUser;
}
