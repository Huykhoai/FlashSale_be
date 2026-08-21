package org.erp.flashsale_be.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    // Optional for first 3 attempts
    private String captchaToken;
    private Integer captchaCode;
}
