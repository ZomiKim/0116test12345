package com.study.spring.member.controller;

import com.study.spring.member.dto.MemberCreateDto;
import com.study.spring.member.dto.MemberResponseDto;
import com.study.spring.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberCreateDto memberCreateDto) {
        try {
            MemberResponseDto member = memberService.createMember(memberCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(member);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 회원 조회 (ID로)
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id) {
        try {
            MemberResponseDto member = memberService.getMember(id);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 회원 조회 (Email로)
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponseDto> getMemberByEmail(@PathVariable String email) {
        try {
            MemberResponseDto member = memberService.getMemberByEmail(email);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
