package com.study.spring.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateDto {

    private String title;
    private String content;
    private Long memberId;
}
