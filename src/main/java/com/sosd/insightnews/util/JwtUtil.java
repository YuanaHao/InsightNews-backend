package com.sosd.insightnews.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final byte[] key = "web@sosd".getBytes();

    public static String createTokenByUserId(String userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        return createToken(payload);
    }

    public static String createToken(Map<String, Object> payload) {
        return JWTUtil.createToken(payload, key);
    }

    /**
     * 解析token
     * @param token
     * @return UserId or null
     */
    public static String getUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        JWTPayload payload = jwt.getPayload();
        if (payload == null) {
            return null;
        }
        Object userId = payload.getClaim("userId");
        if (userId == null) {
            return null;
        }
        return String.valueOf(userId);
    }

    public static boolean verify(String token) {
        return JWTUtil.verify(token, key);
    }
}
