package com.ll.travelmate.guide;

import com.ll.travelmate.guideproposal.GuideProposal;
import com.ll.travelmate.guidereview.GuideReview;
import com.ll.travelmate.user.Gender;
import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Guide {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideId;
    @Min(0)
    @Max(5)
    private Double rating;
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer age;
    private String area;

    @OneToOne
    @JoinColumn(name = "travel_user_id")
    private TravelUser travelUser;

    @OneToMany(mappedBy = "guide")
    List<GuideProposal> guideProposals = new ArrayList<>();
    @OneToMany(mappedBy = "guide")
    List<GuideReview> guideReviews = new ArrayList<>();
}
