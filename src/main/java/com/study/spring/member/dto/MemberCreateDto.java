package com.study.spring.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateDto {

    private String email;
    private String username;
}
