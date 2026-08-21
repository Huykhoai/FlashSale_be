package org.erp.flashsale_be.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "Tên người dùng không được bỏ trống")
    @Size(min = 5, max = 50, message = "Tên người dùng chứa từ 5 đến 50 kí tự")
    String username;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Size(min = 5, max = 50, message = "Mật khẩu chứa từ 5 đến 50 kí tự")
    String password;

    @NotBlank(message = "Email không được bỏ trống")
    @Size(max = 100, message = "Email quá dài không, tối đa 100 kí tự ")
    @Email
    String email;

    @NotBlank(message = "Captcha token không được để trống")
    String captchaToken;

    @NotNull(message = "Mã Captcha không được để trống")
    Integer captchaCode;
}
