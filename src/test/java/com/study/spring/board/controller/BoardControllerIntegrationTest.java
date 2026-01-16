package com.study.spring.board.controller;

import com.study.spring.board.Repository.BoardRepository;
import com.study.spring.board.entity.Board;
import com.study.spring.member.Repository.MemberRepository;
import com.study.spring.member.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class BoardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private User testMember;
    private Long boardId;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = User.builder()
                .email("test@example.com")
                .username("테스트유저")
                .build();
        testMember = memberRepository.save(testMember);

        // 테스트용 게시글 생성
        Board board = Board.builder()
                .title("통합 테스트 게시글")
                .content("통합 테스트 내용")
                .member(testMember)
                .viewCount(0)
                .build();
        Board savedBoard = boardRepository.save(board);
        boardId = savedBoard.getId();
    }

    @Test
    void GET_게시글_목록_조회_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(get("/api/boards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void GET_게시글_상세_조회_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(get("/api/boards/{id}", boardId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.title").value("통합 테스트 게시글"))
                .andExpect(jsonPath("$.content").value("통합 테스트 내용"))
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.viewCount").exists());
    }

    @Test
    void GET_존재하지_않는_게시글_조회_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(get("/api/boards/{id}", 99999L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void POST_게시글_작성_API_테스트() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "files", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/boards")
                        .file(file)
                        .param("title", "새 게시글 제목")
                        .param("content", "새 게시글 내용")
                        .param("memberId", String.valueOf(testMember.getId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void POST_게시글_작성_파일_없이_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/boards")
                        .param("title", "파일 없는 게시글")
                        .param("content", "파일 없는 내용")
                        .param("memberId", String.valueOf(testMember.getId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void POST_게시글_작성_잘못된_회원ID_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/boards")
                        .param("title", "제목")
                        .param("content", "내용")
                        .param("memberId", "99999")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void PUT_게시글_수정_API_테스트() throws Exception {
        // given
        String jsonContent = "{\"title\":\"수정된 제목\",\"content\":\"수정된 내용\"}";

        // when & then
        mockMvc.perform(put("/api/boards/{id}", boardId)
                        .param("memberId", String.valueOf(testMember.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void PUT_게시글_수정_작성자_아닌_경우_API_테스트() throws Exception {
        // given
        User otherMember = User.builder()
                .email("other@example.com")
                .username("다른유저")
                .build();
        User savedOtherMember = memberRepository.save(otherMember);

        // when & then
        String jsonContent = "{\"title\":\"수정 시도\",\"content\":\"수정 시도\"}";
        mockMvc.perform(put("/api/boards/{id}", boardId)
                        .param("memberId", String.valueOf(savedOtherMember.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    void PUT_존재하지_않는_게시글_수정_API_테스트() throws Exception {
        // given
        String jsonContent = "{\"title\":\"수정 시도\",\"content\":\"수정 시도\"}";

        // when & then
        mockMvc.perform(put("/api/boards/{id}", 99999L)
                        .param("memberId", String.valueOf(testMember.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    void DELETE_게시글_삭제_API_테스트() throws Exception {
        // given - 삭제할 게시글 생성
        Board boardToDelete = Board.builder()
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .member(testMember)
                .viewCount(0)
                .build();
        Long deleteBoardId = boardRepository.save(boardToDelete).getId();

        // when & then
        mockMvc.perform(delete("/api/boards/{id}", deleteBoardId)
                        .param("memberId", String.valueOf(testMember.getId())))
                .andExpect(status().isOk());

        // 삭제 확인
        mockMvc.perform(get("/api/boards/{id}", deleteBoardId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void DELETE_게시글_삭제_작성자_아닌_경우_API_테스트() throws Exception {
        // given
        User otherMember = User.builder()
                .email("other@example.com")
                .username("다른유저")
                .build();
        User savedOtherMember = memberRepository.save(otherMember);

        // when & then
        mockMvc.perform(delete("/api/boards/{id}", boardId)
                        .param("memberId", String.valueOf(savedOtherMember.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void DELETE_존재하지_않는_게시글_삭제_API_테스트() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/boards/{id}", 99999L)
                        .param("memberId", String.valueOf(testMember.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void GET_게시글_검색_API_테스트() throws Exception {
        // given
        String keyword = "통합";

        // when & then
        mockMvc.perform(get("/api/boards/search")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("통합 테스트 게시글"));
    }

    @Test
    void GET_게시글_검색_결과없음_API_테스트() throws Exception {
        // given
        String keyword = "존재하지않는키워드12345";

        // when & then
        mockMvc.perform(get("/api/boards/search")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
