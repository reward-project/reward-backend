package com.outsider.reward.domain.member.presentation;

import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody MemberCommand.SignUp command) {
        Long memberId = memberCommandService.signUp(command);
        return ResponseEntity.ok(memberId);
    }
    
    @PostMapping("/login")
    public ResponseEntity<MemberCommand.LoginResponse> login(@RequestBody MemberCommand.Login command) {
        MemberCommand.LoginResponse response = memberCommandService.login(command);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MemberQuery.MemberInfo> getMember(@PathVariable Long id) {
        MemberQuery.MemberInfo memberInfo = memberQueryService.getMemberInfo(id);
        return ResponseEntity.ok(memberInfo);
    }
} 