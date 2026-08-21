package org.erp.flashsale_be.service.implement;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.flashsale_be.constains.RedisKeyPrefix;
import org.erp.flashsale_be.service.CaptchaService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class CaptchaServiceImpl implements CaptchaService {
    StringRedisTemplate redisTemplate;
    private static final char[] OPS = {'+', '-', '*'};

    @Override
    public void generateAndWrite(Long userId, Long eventId, OutputStream out) throws IOException {
        Random rdm = new Random();
        String expression = generateExpression(rdm);
        BufferedImage image = drawImage(expression, rdm);

        int result = evaluate(expression);
        String key = RedisKeyPrefix.getCaptchaKey(userId, eventId);
        redisTemplate.opsForValue().set(key, String.valueOf(result), Duration.ofMinutes(5));
        ImageIO.write(image, "JPEG", out);
    }
    
    @Override
    public boolean verify(Long userId, Long eventId, int inputCode) {
        String key = RedisKeyPrefix.getCaptchaKey(userId, eventId);
        String stored = redisTemplate.opsForValue().get(key);
        if (stored == null) return false;
        if (Integer.parseInt(stored) != inputCode) return false;
        redisTemplate.delete(key);
        return true;
    }

    @Override
    public void generateAndWriteAuthCaptcha(String captchaToken, OutputStream out) throws IOException {
        Random rdm = new Random();
        String expression = generateExpression(rdm);
        BufferedImage image = drawImage(expression, rdm);

        int result = evaluate(expression);
        String key = RedisKeyPrefix.getAuthCaptchaKey(captchaToken);
        redisTemplate.opsForValue().set(key, String.valueOf(result), Duration.ofMinutes(5));
        ImageIO.write(image, "JPEG", out);
    }

    @Override
    public boolean verifyAuthCaptcha(String captchaToken, int inputCode) {
        String key = RedisKeyPrefix.getAuthCaptchaKey(captchaToken);
        String stored = redisTemplate.opsForValue().get(key);
        if (stored == null) return false;
        if (Integer.parseInt(stored) != inputCode) return false;
        redisTemplate.delete(key);
        return true;
    }

    private String generateExpression(Random rdm) {
        int a = rdm.nextInt(10), b = rdm.nextInt(10), c = rdm.nextInt(10);
        char op1 = OPS[rdm.nextInt(3)], op2 = OPS[rdm.nextInt(3)];
        return "" + a + op1 + b + op2 + c;
    }
    private int evaluate(String exp) {
        int a = Character.getNumericValue(exp.charAt(0));
        char op1 = exp.charAt(1);
        int b = Character.getNumericValue(exp.charAt(2));
        char op2 = exp.charAt(3);
        int c = Character.getNumericValue(exp.charAt(4));

        if (op1 == '*' && op2 != '*') return applyOp(op2, a * b, c);
        if (op2 == '*' && op1 != '*') return applyOp(op1, a, b * c);
        if (op1 == '*' && op2 == '*') return a * b * c;
        return applyOp(op2, applyOp(op1, a, b), c);
    }
    private int applyOp(char op, int x, int y) {
        return switch (op) {
            case '+' -> x + y;
            case '-' -> x - y;
            case '*' -> x * y;
            default -> 0;
        };
    }
    private BufferedImage drawImage(String expression, Random rdm) {
        int width = 80, height = 32;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        for (int i = 0; i < 50; i++) {
            g.drawOval(rdm.nextInt(width), rdm.nextInt(height), 0, 0);
        }
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(expression, 8, 24);
        g.dispose();
        return image;
    }
}
