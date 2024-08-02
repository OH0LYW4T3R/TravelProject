package com.ll.travelmate.guide;

import com.ll.travelmate.user.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GuideDto {
    private Long guideId;
    private Double rating;
    private String name;
    private Gender gender; // Enum을 String으로 변환하여 담기
    private Integer age;
    private String area;
    private Double totalValue;
    private Integer reviewCounter;
//    private Long travelUserId; // TravelUser의 ID만 저장
//    private List<Long> guideProposalIds; // GuideProposal의 ID 리스트
//    private List<Long> guideReviewIds; // GuideReview의 ID 리스트
}
