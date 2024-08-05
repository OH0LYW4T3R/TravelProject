package com.ll.travelmate.proposal;

import com.ll.travelmate.travel.Travel;
import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalId;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_user_id") // 외래키를 가지고 있는 객체가 연관관계의 주인이다.
    private TravelUser travelUser;
    @OneToOne // 단방향
    @JoinColumn(name = "offerd_travel_user_id", unique = false)
    private TravelUser offeredTravelUser;
    @OneToOne // 단방향
    @JoinColumn(name = "offer_travel_id", unique = false)
    private Travel travel;
    private LocalDateTime refuseTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private ProposalStatus proposalStatus;
}
