package com.study.spring.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListDto {

    private Long id;
    private String title;
    private String username;
    private Integer viewCount;
    private LocalDateTime createdAt;

    // Entity에서 DTO로 변환하는 정적 메서드
    public static BoardListDto fromEntity(com.study.spring.board.entity.Board board) {
        return BoardListDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .username(board.getMember().getUsername())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .build();
    }
}
