package com.sosd.insightnews.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AliService{

    void sendCode(String phone) throws Exception;

    void uploadAvatar(String avatarBase64, String id);

    String getAvatar(String id);

    CompletableFuture<String> uploadFile(MultipartFile file) throws IOException;
}
