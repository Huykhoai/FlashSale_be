package org.erp.flashsale_be.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.annotation.AccessLimit;
import org.erp.flashsale_be.security.UserPrincipal;
import org.erp.flashsale_be.service.CaptchaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/captcha/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CaptchaController {
    CaptchaService captchaService;

    @GetMapping("/{eventId}")
    @AccessLimit(seconds = 5, maxCount = 3, needLogin = true)
    public void getCaptcha(@PathVariable("eventId") Long eventId,
                           @AuthenticationPrincipal UserPrincipal principal,
                           HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-store, no-cache");
        captchaService.generateAndWrite(principal.getId(), eventId, response.getOutputStream());
    }

}
