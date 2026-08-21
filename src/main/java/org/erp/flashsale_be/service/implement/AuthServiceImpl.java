package org.erp.flashsale_be.service.implement;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.constains.RedisKeyPrefix;
import org.erp.flashsale_be.entity.FsUser;
import org.erp.flashsale_be.repository.FsUserRepository;
import org.erp.flashsale_be.request.LoginRequest;
import org.erp.flashsale_be.request.RegisterRequest;
import org.erp.flashsale_be.response.LoginRes;
import org.erp.flashsale_be.response.Message;
import org.erp.flashsale_be.security.JwtTokenProvider;
import org.erp.flashsale_be.security.UserPrincipal;
import org.erp.flashsale_be.service.AuthService;
import org.erp.flashsale_be.service.CaptchaService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;
    PasswordEncoder passwordEncoder;
    UserDetailsService userDetailsService;
    StringRedisTemplate redisTemplate;
    FsUserRepository fsUserRepository;
    CaptchaService captchaService;

    @Override
    public LoginRes login(LoginRequest request, HttpServletResponse response) {
        String username = request.getUsername();
        String failKey = RedisKeyPrefix.getLoginFailKey(username);

        String failCountStr = redisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr != null ? Integer.parseInt(failCountStr) : 0;

        if (failCount >= 3) {
            if (request.getCaptchaToken() == null || request.getCaptchaCode() == null) {
                throw new BadCredentialsException("Vui lòng nhập mã xác nhận (Captcha)");
            }
            boolean isCaptchaValid = captchaService.verifyAuthCaptcha(request.getCaptchaToken(), request.getCaptchaCode());
            if (!isCaptchaValid) {
                throw new BadCredentialsException("Mã xác nhận không đúng hoặc đã hết hạn");
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, Duration.ofMinutes(15));
            throw new BadCredentialsException("Mật khẩu hoặc tài khoản không chính xác");
        }

        redisTemplate.delete(failKey);

        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtTokenProvider.generateToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        setTokenCookie(response, token, "token");
        setTokenCookie(response, refreshToken, "refresh_token");

        return LoginRes.builder().username(principal.getUsername()).token(token).build();
    }

    @Override
    public Message register(RegisterRequest request) {

        if (request.getCaptchaToken() == null || request.getCaptchaCode() == null) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Vui lòng nhập mã xác nhận (Captcha)");
        }
        boolean isCaptchaValid = captchaService.verifyAuthCaptcha(request.getCaptchaToken(), request.getCaptchaCode());
        if (!isCaptchaValid) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Mã xác nhận không đúng hoặc đã hết hạn");
        }

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String email = request.getEmail().trim();

        if (fsUserRepository.existsFsUserByUsernameAndIsDisable(username, false)) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Tên tài khoản đã tồn tại");
        }

        if (fsUserRepository.existsFsUserByEmailAndIsDisable(email, false)) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Email đã tồn tại");
        }

        FsUser fsUser = FsUser.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .isDisable(false)
                .build();
        fsUserRepository.save(fsUser);

        return new Message(HttpStatus.OK.value(), "Đăng ký tài khoản thành công");
    }

    private void setTokenCookie(HttpServletResponse response, String token, String key) {
        ResponseCookie cookie = ResponseCookie.from(key, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
