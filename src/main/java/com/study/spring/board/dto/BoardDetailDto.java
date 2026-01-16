package com.study.spring.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailDto {

    private Long id;
    private String title;
    private String content;
    private String username;
    private String email;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ImageDto> images;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDto {
        private Long id;
        private String filename;
        private String filepath;
    }

    // Entity에서 DTO로 변환하는 정적 메서드
    public static BoardDetailDto fromEntity(com.study.spring.board.entity.Board board) {
        List<ImageDto> imageDtos = board.getImages().stream()
                .map(image -> ImageDto.builder()
                        .id(image.getId())
                        .filename(image.getFilename())
                        .filepath(image.getFilepath())
                        .build())
                .collect(Collectors.toList());

        return BoardDetailDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getMember().getUsername())
                .email(board.getMember().getEmail())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .images(imageDtos)
                .build();
    }
}
