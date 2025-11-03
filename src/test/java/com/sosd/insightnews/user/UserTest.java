package com.sosd.insightnews.user;

import com.sosd.insightnews.InsightNewsApplication;
import com.sosd.insightnews.domain.R;
import com.sosd.insightnews.dto.LoginDTO;
import com.sosd.insightnews.dto.UpdateUserDTO;
import com.sosd.insightnews.service.UserService;
import com.sosd.insightnews.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {InsightNewsApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserTest {

    protected RestTemplate restTemplate = (new TestRestTemplate()).getRestTemplate();

    @Autowired
    private UserService userService;

    @Test
    void sendCode() {
        try {
            HttpHeaders headers = new HttpHeaders();

//            String phone = "13709040302";
            String phone = "15259990678";

            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<R> response = restTemplate.exchange(
                    "http://localhost:8087/common/code?phone={phone}",
                    HttpMethod.POST,
                    entity,
                    R.class,
                    phone);
            System.out.println("response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void testLogin() {
        try {
            HttpHeaders headers = new HttpHeaders();

            LoginDTO req = new LoginDTO();
            req.setPhone("18960935500");
            req.setCode("123456");

            HttpEntity<LoginDTO> entity = new HttpEntity<>(req, headers);

            ResponseEntity<R> resp = restTemplate.exchange("http://localhost:8087/user/login", HttpMethod.POST, entity, R.class);
            System.out.println("token: " + Objects.requireNonNull(resp.getBody()).getData());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testRegister() {
        try {
            HttpHeaders headers = new HttpHeaders();

            LoginDTO req = new LoginDTO();
            req.setPhone("13709040302");
            req.setCode("123456");  // 测试验证码

            HttpEntity<LoginDTO> entity = new HttpEntity<>(req, headers);

            ResponseEntity<R> resp = restTemplate.exchange("http://localhost:8087/user/register", HttpMethod.POST, entity, R.class);
            System.out.println("token: " + Objects.requireNonNull(resp.getBody()).getData());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testGetUserInfo() {
        try {
            String token = JwtUtil.createTokenByUserId("18960935500");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            System.out.println("token: " + token);

            HttpEntity<Void> entity = new HttpEntity<>(null, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/user/info",
                HttpMethod.GET,
                entity,
                R.class
            );

            System.out.println("User info: " + resp.getBody().getData());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testUpdate() {
        try {
            String token = JwtUtil.createTokenByUserId("18960935500");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            UpdateUserDTO req = new UpdateUserDTO();
            req.setName("测试更改");
            req.setGender("女");
            req.setRegion("福建 福州");
            req.setProfile("这是一个测试账号");
            req.setEmail("2071908434@qq.com");

            HttpEntity<UpdateUserDTO> entity = new HttpEntity<>(req, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/user/update",
                HttpMethod.PUT,
                entity,
                R.class
            );

            System.out.println("Update response: " + resp.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testLogout() {
        try {
            String token = JwtUtil.createTokenByUserId("18960935500");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(null, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/user/logout",
                HttpMethod.POST,
                entity,
                R.class
            );

            System.out.println("Logout response: " + resp.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testDelete() {
        try {
            String token = JwtUtil.createTokenByUserId("13709040302");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(null, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/user/delete",
                HttpMethod.DELETE,
                entity,
                R.class
            );

            System.out.println("Delete response: " + resp.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testFeedback() {
        try {
            String token = JwtUtil.createTokenByUserId("18960935500");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            String feedback = "这是一条测试反馈信息";
            HttpEntity<String> entity = new HttpEntity<>(feedback, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/user/feedback?feedback={feedback}",
                HttpMethod.POST,
                entity,
                R.class,
                feedback
            );

            System.out.println("Feedback response: " + resp.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testUploadFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // 创建文件资源
            File file = new File("src/test/java/com/sosd/insightnews/user/testAvatar.JPG");
            FileSystemResource resource = new FileSystemResource(file);

            // 创建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource); // 假设服务器的参数名为 "file"

            // 创建请求实体
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<R> resp = restTemplate.exchange(
                "http://localhost:8087/common/upload",
                HttpMethod.POST,
                entity,
                R.class
            );

            System.out.println("Upload response: " + resp.getBody());

            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
