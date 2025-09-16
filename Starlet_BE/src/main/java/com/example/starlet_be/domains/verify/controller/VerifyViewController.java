package com.example.starlet_be.domains.verify.controller;

import com.example.starlet_be.domains.verify.service.VerifyService;
import com.example.starlet_be.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/v1/verify")
public class VerifyViewController {
    private final VerifyService verifyService;

    /**
     * 초기 회원가입 이메일 인증을 처리하고 결과를 뷰로 보여줍니다.
     */
    @GetMapping("/init")
    public String emailVerification(@RequestParam String token, Model model) {
        try {
            // 1. VerifyService를 호출하여 토큰 인증 로직을 수행합니다.
            verifyService.emailVerification(token);

            // 2. 인증 성공 시, 뷰 템플릿에 전달할 데이터를 Model에 추가합니다.
            model.addAttribute("title", "✨ 회원가입 인증 완료");
            model.addAttribute("message", "별빛의 안내가 끝났습니다. Starlet의 세계에 오신 것을 환영합니다.");

            // 3. 성공 페이지(verification-success.html)를 반환합니다.
            return "verification-success";

        } catch (CustomException e) {
            // 4. 인증 실패 시, 에러 메시지를 Model에 추가합니다.
            model.addAttribute("message", e.getErrorCode().getMessage());

            // 5. 실패 페이지(verification-error.html)를 반환합니다.
            return "verification-error";
        }
    }

    /**
     * 비밀번호 재설정 이메일 인증을 처리하고 결과를 뷰로 보여줍니다.
     */
    @GetMapping("/password-reset/confirm")
    public String passwordResetVerification(@RequestParam String token, Model model) {
        try {
            verifyService.passwordResetVerification(token);
            model.addAttribute("title", "🔑 비밀번호 재설정 허용");
            model.addAttribute("message", "새로운 길을 열 준비가 되었습니다. 앱/웹으로 돌아가 새 비밀번호를 설정해주세요.");

            // 비밀번호 재설정 전용 성공 페이지를 반환합니다.
            return "password-reset-success";

        } catch (CustomException e) {
            model.addAttribute("message", e.getErrorCode().getMessage());
            return "verification-error";
        }
    }
}
