package com.ll.travelmate.member;

import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @OneToOne
    @JoinColumn(name = "travel_user_id") // name은 외래키의 컬럼 이름이다.
    private TravelUser travelUser;
}