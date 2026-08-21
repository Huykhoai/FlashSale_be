package org.erp.flashsale_be.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.annotation.AccessLimit;
import org.erp.flashsale_be.constains.RedisKeyPrefix;
import org.erp.flashsale_be.exception.AuthenticationException;
import org.erp.flashsale_be.exception.RateLimitException;
import org.erp.flashsale_be.service.RateLimiterService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessLimitInterceptor implements HandlerInterceptor {
    RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }

        AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
        if (accessLimit == null) {
            return true;
        }

        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        boolean needLogin = accessLimit.needLogin();

        String key = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal  userDetails = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            userDetails = (UserPrincipal) authentication.getPrincipal();
        }

        String identifier;
        if (needLogin) {
            if (userDetails == null) {
                throw new AuthenticationException("Chưa đăng nhập");
            }
            identifier = String.valueOf(userDetails.getId());
        } else {
                identifier = request.getRemoteAddr();
        }

        String redisKey = RedisKeyPrefix.getAccessLimitKey(key, identifier);
        boolean isAllowed = rateLimiterService.isAllowed(redisKey, maxCount, seconds);

        if (Boolean.FALSE.equals(isAllowed)) {
            throw new RateLimitException("Thao tác quá nhanh, vui lòng thử lại sau " + seconds / 60);
        }

        return true;
    }
}
