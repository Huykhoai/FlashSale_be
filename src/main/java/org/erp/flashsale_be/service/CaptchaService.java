package org.erp.flashsale_be.service;

import java.io.IOException;
import java.io.OutputStream;

public interface CaptchaService {
    void generateAndWrite(Long userId, Long eventId, OutputStream out) throws IOException;
    boolean verify(Long userId, Long eventId, int inputCode);
    void generateAndWriteAuthCaptcha(String captchaToken, OutputStream out) throws IOException;
    boolean verifyAuthCaptcha(String captchaToken, int inputCode);
}
