package com.sosd.insightnews.auth.interceptor;


import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.UserService;
import com.sosd.insightnews.service.impl.UserServiceImpl;
import com.sosd.insightnews.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取用户信息
        String header = request.getHeader("Authorization");
        if (header == null) {
            throw new BadRequestException("未登录");
        }

        if (!JwtUtil.verify(header)) {
            throw new BadRequestException("token验证失败 请重新登录");
        }

        String userId = JwtUtil.getUserId(header);
        if (userId == null) {
            throw new BadRequestException("token验证失败 请重新登录");
        }
        // String requestURI = request.getRequestURI();
        // System.out.println("Request URI: " + requestURI + ", User ID: " + userId);

        UserDo userDo = userService.getUserById(userId);

        if (userDo == null) {
            throw new BadRequestException("用户不存在");
        }

        UserContext.setCurrentUser(userDo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.clear();
    }
}
