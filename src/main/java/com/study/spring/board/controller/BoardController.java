package com.study.spring.board.controller;

import com.study.spring.board.dto.BoardCreateDto;
import com.study.spring.board.dto.BoardDetailDto;
import com.study.spring.board.dto.BoardListDto;
import com.study.spring.board.dto.BoardUpdateDto;
import com.study.spring.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<BoardListDto>> getBoardList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BoardListDto> boardList = boardService.getBoardList(pageable);
            return ResponseEntity.ok(boardList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailDto> getBoardDetail(@PathVariable Long id) {
        try {
            BoardDetailDto boardDetail = boardService.getBoardDetail(id);
            return ResponseEntity.ok(boardDetail);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Long> createBoard(
            @ModelAttribute BoardCreateDto boardCreateDto,
            @RequestParam(required = false) List<MultipartFile> files) {
        try {
            Long boardId = boardService.createBoard(boardCreateDto, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(boardId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardUpdateDto boardUpdateDto,
            @RequestParam Long memberId) {
        try {
            boardService.updateBoard(id, boardUpdateDto, memberId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            @RequestParam Long memberId) {
        try {
            boardService.deleteBoard(id, memberId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<Page<BoardListDto>> searchBoards(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BoardListDto> boardList = boardService.searchBoards(keyword, pageable);
            return ResponseEntity.ok(boardList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
