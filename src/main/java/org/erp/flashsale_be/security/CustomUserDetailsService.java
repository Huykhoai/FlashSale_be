package org.erp.flashsale_be.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.constains.RedisKeyPrefix;
import org.erp.flashsale_be.entity.FsUser;
import org.erp.flashsale_be.repository.FsUserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {

    StringRedisTemplate redisTemplate;
    FsUserRepository fsUserRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername( String username) throws UsernameNotFoundException {
        String key = RedisKeyPrefix.getUserInfoKey(username);
        String cachedUserJson = redisTemplate.opsForValue().get(key);

        try {
            if (cachedUserJson != null) {
                FsUser user = Json.mapper().readValue(cachedUserJson, FsUser.class);
                return new UserPrincipal(user);
            }

            FsUser fsUser = fsUserRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));
            redisTemplate.opsForValue().set(key, Json.mapper().writeValueAsString(fsUser));

            return new UserPrincipal(fsUser);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi chuyển đổi JSON khi đọc/ghi Redis", e);
        }
    }
}
