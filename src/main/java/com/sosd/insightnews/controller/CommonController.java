package com.sosd.insightnews.controller;

import com.sosd.insightnews.domain.R;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.AliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliService aliService;

    private static final String phone_regex = "^1[3-9]\\d{9}$";

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 验证码
     **/
    @RequestMapping(value = "/code",method = RequestMethod.POST)
    public R<Object> sendCode(String phone) throws Exception {
        if (!phone.matches(phone_regex)) {
            throw new BadRequestException("手机号格式错误");
        }
        aliService.sendCode(phone);
        return R.ok("验证码发送成功",null);
    }

    /**
     * 文件上传
     * @param file 文件
     * @return 文件路径
     **/
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public R<String> upload(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<String> url = aliService.uploadFile(file);
        return R.ok("文件上传成功",url.get());
    }
}
