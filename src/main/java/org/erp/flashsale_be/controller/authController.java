package org.erp.flashsale_be.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.annotation.AccessLimit;
import org.erp.flashsale_be.common.ApiResponse;
import org.erp.flashsale_be.request.LoginRequest;
import org.erp.flashsale_be.request.RegisterRequest;
import org.erp.flashsale_be.response.LoginRes;
import org.erp.flashsale_be.response.Message;
import org.erp.flashsale_be.service.AuthService;
import org.erp.flashsale_be.service.CaptchaService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class authController {
    AuthService authService;
    CaptchaService captchaService;

    @GetMapping("/captcha/{captchaToken}")
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = false)
    public void getAuthCaptcha(@PathVariable("captchaToken") String captchaToken,
                               HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-store, no-cache");
        captchaService.generateAndWriteAuthCaptcha(captchaToken, response.getOutputStream());
    }

    @PostMapping("/login")
    @AccessLimit(maxCount = 5, seconds = 600, needLogin = false)
    public ApiResponse<LoginRes> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ApiResponse.success(authService.login(request, response));
    }

    @PostMapping("/register")
    @AccessLimit(maxCount = 5, seconds = 600, needLogin = false)
    public ApiResponse<Message> register(
            @Valid @RequestBody RegisterRequest request
    ){
        return ApiResponse.success(authService.register(request));
    }
}
