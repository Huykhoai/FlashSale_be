package org.erp.flashsale_be.service;

import jakarta.servlet.http.HttpServletResponse;
import org.erp.flashsale_be.request.LoginRequest;
import org.erp.flashsale_be.request.RegisterRequest;
import org.erp.flashsale_be.response.LoginRes;
import org.erp.flashsale_be.response.Message;

public interface AuthService {

    LoginRes login(LoginRequest request, HttpServletResponse response);
    Message register(RegisterRequest request);
}
