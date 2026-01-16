package com.study.spring.board.service;

import com.study.spring.board.Repository.BoardRepository;
import com.study.spring.board.dto.BoardCreateDto;
import com.study.spring.board.dto.BoardDetailDto;
import com.study.spring.board.dto.BoardListDto;
import com.study.spring.board.dto.BoardUpdateDto;
import com.study.spring.board.entity.Board;
import com.study.spring.member.Repository.MemberRepository;
import com.study.spring.member.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private User testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = User.builder()
                .email("test@example.com")
                .username("테스트유저")
                .build();
        testMember = memberRepository.save(testMember);
    }

    @Test
    void 게시글_생성_테스트() throws IOException {
        // given
        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .memberId(testMember.getId())
                .build();

        // when
        Long createdBoardId = boardService.createBoard(createDto, null);

        // then
        assertThat(createdBoardId).isNotNull();
        Board savedBoard = boardRepository.findById(createdBoardId).orElseThrow();
        assertThat(savedBoard.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedBoard.getContent()).isEqualTo("테스트 내용");
        assertThat(savedBoard.getMember().getId()).isEqualTo(testMember.getId());
    }

    @Test
    void 게시글_조회_및_조회수_증가_테스트() throws IOException {
        // given
        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("조회 테스트")
                .content("조회 내용")
                .memberId(testMember.getId())
                .build();
        Long createdBoardId = boardService.createBoard(createDto, null);
        Board beforeBoard = boardRepository.findById(createdBoardId).orElseThrow();
        int beforeViewCount = beforeBoard.getViewCount();

        // when
        BoardDetailDto detailDto = boardService.getBoardDetail(createdBoardId);

        // then
        assertThat(detailDto.getId()).isEqualTo(createdBoardId);
        assertThat(detailDto.getTitle()).isEqualTo("조회 테스트");
        Board afterBoard = boardRepository.findById(createdBoardId).orElseThrow();
        assertThat(afterBoard.getViewCount()).isEqualTo(beforeViewCount + 1);
    }

    @Test
    void 게시글_목록_조회_테스트() throws IOException {
        // given
        BoardCreateDto createDto1 = BoardCreateDto.builder()
                .title("목록 테스트 1")
                .content("내용 1")
                .memberId(testMember.getId())
                .build();
        BoardCreateDto createDto2 = BoardCreateDto.builder()
                .title("목록 테스트 2")
                .content("내용 2")
                .memberId(testMember.getId())
                .build();
        boardService.createBoard(createDto1, null);
        boardService.createBoard(createDto2, null);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BoardListDto> result = boardService.getBoardList(pageable);

        // then
        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void 게시글_수정_테스트() throws IOException {
        // given
        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("수정 전 제목")
                .content("수정 전 내용")
                .memberId(testMember.getId())
                .build();
        Long createdBoardId = boardService.createBoard(createDto, null);

        BoardUpdateDto updateDto = BoardUpdateDto.builder()
                .title("수정 후 제목")
                .content("수정 후 내용")
                .build();

        // when
        boardService.updateBoard(createdBoardId, updateDto, testMember.getId());

        // then
        Board updatedBoard = boardRepository.findById(createdBoardId).orElseThrow();
        assertThat(updatedBoard.getTitle()).isEqualTo("수정 후 제목");
        assertThat(updatedBoard.getContent()).isEqualTo("수정 후 내용");
    }

    @Test
    void 게시글_수정_작성자_검증_테스트() throws IOException {
        // given
        User otherMember = User.builder()
                .email("other@example.com")
                .username("다른유저")
                .build();
        User savedOtherMember = memberRepository.save(otherMember);

        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("수정 테스트")
                .content("내용")
                .memberId(testMember.getId())
                .build();
        Long createdBoardId = boardService.createBoard(createDto, null);

        BoardUpdateDto updateDto = BoardUpdateDto.builder()
                .title("수정 시도")
                .content("수정 시도")
                .build();

        // when & then
        assertThatThrownBy(() -> 
            boardService.updateBoard(createdBoardId, updateDto, savedOtherMember.getId())
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("작성자만 수정할 수 있습니다");
    }

    @Test
    void 게시글_삭제_테스트() throws IOException {
        // given
        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("삭제 테스트")
                .content("삭제 내용")
                .memberId(testMember.getId())
                .build();
        Long createdBoardId = boardService.createBoard(createDto, null);

        // when
        boardService.deleteBoard(createdBoardId, testMember.getId());

        // then
        assertThat(boardRepository.findById(createdBoardId)).isEmpty();
    }

    @Test
    void 게시글_삭제_작성자_검증_테스트() throws IOException {
        // given
        User otherMember = User.builder()
                .email("other@example.com")
                .username("다른유저")
                .build();
        User savedOtherMember = memberRepository.save(otherMember);

        BoardCreateDto createDto = BoardCreateDto.builder()
                .title("삭제 테스트")
                .content("내용")
                .memberId(testMember.getId())
                .build();
        Long createdBoardId = boardService.createBoard(createDto, null);

        // when & then
        assertThatThrownBy(() -> 
            boardService.deleteBoard(createdBoardId, savedOtherMember.getId())
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("작성자만 삭제할 수 있습니다");
    }
}
