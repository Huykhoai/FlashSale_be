package org.erp.flashsale_be.constains;

public class RedisKeyPrefix {
    public static String getFlashStockKey(Long eventId) {
        return "flash:stock:" + eventId;
    }
    public static String getSecretPathKey(Long userId, Long eventId) {
        return "flash:path:" + userId + ":" + eventId;
    }
    public static String getCaptchaKey(Long userId, Long eventId) {
        return "captcha:" + userId + ":" + eventId;
    }
    public static String getAuthCaptchaKey(String captchaToken) {
        return "captcha:auth:" + captchaToken;
    }
    public static String getAccessLimitKey(String uri, String identifier) {
        return "access:" + uri + "_" + identifier;
    }
    public static String getOrderCacheKey(Long userId, Long goodsId) {
        return "order:cache:" + userId + ":" + goodsId;
    }
    public static String getUserInfoKey(String username) { return "user:info:" + username; }
    public static String getLoginFailKey(String username) { return "login:fail:" + username; }
}
