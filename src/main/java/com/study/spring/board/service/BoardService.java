package com.study.spring.board.service;

import com.study.spring.board.Repository.BoardRepository;
import com.study.spring.board.dto.BoardCreateDto;
import com.study.spring.board.dto.BoardDetailDto;
import com.study.spring.board.dto.BoardListDto;
import com.study.spring.board.dto.BoardUpdateDto;
import com.study.spring.board.entity.Board;
import com.study.spring.board.entity.Image;
import com.study.spring.member.Repository.MemberRepository;
import com.study.spring.member.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    // 게시글 목록 조회 (페이징)
    public Page<BoardListDto> getBoardList(Pageable pageable) {
        Page<Board> boards = boardRepository.findAllWithMemberAndImages(pageable);
        return boards.map(BoardListDto::fromEntity);
    }

    // 게시글 상세 조회 (조회수 증가)
    @Transactional
    public BoardDetailDto getBoardDetail(Long id) {
        Board board = boardRepository.findByIdWithMemberAndImages(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 조회수 증가
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
        
        return BoardDetailDto.fromEntity(board);
    }

    // 게시글 생성 (파일 업로드 포함)
    @Transactional
    public Long createBoard(BoardCreateDto boardCreateDto, List<MultipartFile> files) throws IOException {
        User member = memberRepository.findById(boardCreateDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(boardCreateDto.getTitle())
                .content(boardCreateDto.getContent())
                .member(member)
                .viewCount(0)
                .images(new ArrayList<>())
                .build();

        // 이미지 파일 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filepath = fileService.saveImageWithThumbnail(file);
                    String filename = file.getOriginalFilename();
                    
                    Image image = Image.builder()
                            .filename(filename != null ? filename : "")
                            .filepath(filepath)
                            .board(board)
                            .build();
                    
                    board.getImages().add(image);
                }
            }
        }

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    // 게시글 수정
    @Transactional
    public void updateBoard(Long id, BoardUpdateDto boardUpdateDto, Long memberId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 본인만 수정 가능
        if (!board.getMember().getId().equals(memberId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        board.setTitle(boardUpdateDto.getTitle());
        board.setContent(boardUpdateDto.getContent());
        boardRepository.save(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id, Long memberId) throws IOException {
        Board board = boardRepository.findByIdWithMemberAndImages(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 본인만 삭제 가능
        if (!board.getMember().getId().equals(memberId)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        // 이미지 파일 삭제
        for (Image image : board.getImages()) {
            fileService.deleteFile(image.getFilepath());
        }

        boardRepository.delete(board);
    }

    // 게시글 검색
    public Page<BoardListDto> searchBoards(String keyword, Pageable pageable) {
        Page<Board> boards = boardRepository.searchByKeyword(keyword, pageable);
        return boards.map(BoardListDto::fromEntity);
    }
}
