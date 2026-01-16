package com.study.spring.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> testPage() {
        return ResponseEntity.ok("Board API 테스트 페이지입니다.");
    }
}
