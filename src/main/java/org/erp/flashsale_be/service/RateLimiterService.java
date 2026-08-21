package org.erp.flashsale_be.service;

public interface RateLimiterService {
    boolean isAllowed(String key, int limit, int windowInSeconds);

    boolean isBlacklisted(String ip);

    void blacklistIp(String ip, long durationInSeconds);

    long incrementViolations(String ip, int windowInSeconds);
}
