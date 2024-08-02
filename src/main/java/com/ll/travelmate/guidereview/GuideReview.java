package com.ll.travelmate.guidereview;

import com.ll.travelmate.guide.Guide;
import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class GuideReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideReviewId;
    private String comment;
    @Min(0) @Max(5)
    private Double rating;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long guideProposalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @OneToOne // 단방향
    @JoinColumn(name = "write_travel_user_id")
    private TravelUser travelUser;
}
