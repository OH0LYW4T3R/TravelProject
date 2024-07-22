package com.ll.travelmate.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ll.travelmate.block.BlockDto;
import com.ll.travelmate.friend.FriendDto;
import com.ll.travelmate.guide.GuideDto;
import com.ll.travelmate.member.MemberDto;
import com.ll.travelmate.travel.TravelDto;
import com.ll.travelmate.travelsetting.TravelSettingDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class TravelUserDto {
    // 요청 응답시 유용하게 사용되는 DTO (직렬화 역직렬화 대신해주는 것이라고 생각하면 됨.)
    private Long travelUserId;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Age is required")
    private Integer age;
    private String address;
    @NotNull(message = "PhoneNumber is required")
    private String phoneNumber;
    private String imageUrl;
    private String introduction;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    // 연관된 엔티티 DTO
    private MemberDto member;
    private GuideDto guide;
    private TravelSettingDto travelSetting;
    private List<FriendDto> friends;
    private List<BlockDto> blocks;
    private List<TravelDto> travels;
}
