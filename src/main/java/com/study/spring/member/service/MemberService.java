package com.study.spring.member.service;

import com.study.spring.member.Repository.MemberRepository;
import com.study.spring.member.dto.MemberCreateDto;
import com.study.spring.member.dto.MemberResponseDto;
import com.study.spring.member.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public MemberResponseDto createMember(MemberCreateDto memberCreateDto) {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(memberCreateDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User member = User.builder()
                .email(memberCreateDto.getEmail())
                .username(memberCreateDto.getUsername())
                .build();

        User savedMember = memberRepository.save(member);
        return MemberResponseDto.fromEntity(savedMember);
    }

    // 회원 조회 (ID로)
    public MemberResponseDto getMember(Long id) {
        User member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        return MemberResponseDto.fromEntity(member);
    }

    // 회원 조회 (Email로)
    public MemberResponseDto getMemberByEmail(String email) {
        User member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        return MemberResponseDto.fromEntity(member);
    }
}
