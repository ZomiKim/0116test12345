package com.study.spring.board.Repository;

import com.study.spring.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.images " +
           "ORDER BY b.createdAt DESC")
    Page<Board> findAllWithMemberAndImages(Pageable pageable);

    @Query("SELECT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.images " +
           "WHERE b.id = :id")
    java.util.Optional<Board> findByIdWithMemberAndImages(@Param("id") Long id);

    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.images " +
           "WHERE b.title LIKE CONCAT('%', :keyword, '%') OR b.content LIKE CONCAT('%', :keyword, '%') " +
           "ORDER BY b.createdAt DESC")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
