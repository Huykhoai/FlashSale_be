package org.erp.flashsale_be.service.implement;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.service.RateLimiterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateLimiterServiceImpl implements RateLimiterService {

    StringRedisTemplate redisTemplate;

    private static final String LUA_SCRIPT = "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local window = tonumber(ARGV[2]) " +
            "local current = redis.call('INCR', key) " +
            "if current == 1 then " +
            "    redis.call('EXPIRE', key, window) " +
            "end " +
            "return current <= limit";

    /**
     * Checks if a request is allowed.
     *
     * @param key             Unique key (IP address)
     * @param limit           Max requests allowed
     * @param windowInSeconds Time window in seconds
     * @return true if allowed, false if rate limited
     */
    @Override
    public boolean isAllowed(String key, int limit, int windowInSeconds) {
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>(LUA_SCRIPT, Boolean.class);
        return Boolean.TRUE.equals(redisTemplate.execute(script, Collections.singletonList(key),
                String.valueOf(limit), String.valueOf(windowInSeconds)));
    }

    @Override
    public boolean isBlacklisted(String ip) {
        return false;
    }

    @Override
    public void blacklistIp(String ip, long durationInSeconds) {

    }

    @Override
    public long incrementViolations(String ip, int windowInSeconds) {
        return 0;
    }
}
