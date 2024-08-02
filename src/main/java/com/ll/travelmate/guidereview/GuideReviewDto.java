package com.ll.travelmate.guidereview;

import com.ll.travelmate.guide.GuideDto;
import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GuideReviewDto {
    private Long guideReviewId;
    private Long guideProposalId;
    private GuideDto guide;
    private TravelUserDto writerTravelUser;
    private String comment;
    private Double rating;
    private LocalDateTime createdAt;
}
