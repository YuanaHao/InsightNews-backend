package com.sosd.insightnews.controller;

import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.converter.UserConverter;
import com.sosd.insightnews.domain.R;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.dto.LoginDTO;
import com.sosd.insightnews.dto.TopicDTO;
import com.sosd.insightnews.dto.UpdateUserDTO;
import com.sosd.insightnews.dto.UserDTO;
import com.sosd.insightnews.email.EmailService;
import com.sosd.insightnews.email.builder.FeedbackEmailBuilder;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.ScienceTopicService;
import com.sosd.insightnews.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ScienceTopicService scienceTopicService;

    private static final String phone_regex = "^1[3-9]\\d{9}$";
    private static final String email_regex = "^(default|[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)$";

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录成功后的token
     **/
    @PostMapping("/login")
    public R<String> login(@RequestBody LoginDTO loginDTO) {
        String phone = loginDTO.getPhone();
        if (!phone.matches(phone_regex)) {
            throw new BadRequestException("手机号格式错误");
        }
        String token = userService.login(loginDTO);
        return R.ok("用户登录成功",token);
    }

    /**
     * 用户注册
     * @param loginDTO 注册信息
     * @return 注册成功后的token
     **/
    @PostMapping("/register")
    public R<String> register(@RequestBody LoginDTO loginDTO) throws BadRequestException{
        String phone = loginDTO.getPhone();
        if (!phone.matches(phone_regex)) {
            throw new BadRequestException("手机号格式错误");
        }
        String token = userService.register(loginDTO);
        return R.ok("用户注册成功",token);
    }

    /**
     * 获取用户信息
     * @return 用户信息
     **/
    @GetMapping("/info")
    public R<UserDTO> getUserInfo() throws IOException {
        UserDo userDo = UserContext.getCurrentUser();
        UserDTO result = UserConverter.do2dto(userDo);
        return R.ok("获取用户信息成功",result);
    }

    /**
     * 修改用户信息
     * @param updateUserDTO 修改信息
     * @return 修改成功后的用户信息
     **/
    @PutMapping("/update")
    @Transactional
    public R<String> update(@RequestBody UpdateUserDTO updateUserDTO){
        UserDo userDo = UserContext.getCurrentUser();
        if(!updateUserDTO.getEmail().equals("default")){
            if(!updateUserDTO.getEmail().matches(email_regex)){
                throw new BadRequestException("邮箱格式错误");
            }
        }
        UserDo o = UserConverter.dto2do(updateUserDTO);
        o.setId(userDo.getId());
        userService.updateInfo(o);
        log.info("Users modify personal information：{}",updateUserDTO);
        return R.ok("修改用户信息成功",null);
    }

    /**
     * 用户退出登录
     * @return 退出成功信息
     **/
    @PostMapping("/logout")
    public R<Object> logout(){
        userService.logout();
        return R.ok("用户退出登录",null);
    }

    /**
     * 用户注销
     * @return 注销成功信息
     **/
    @DeleteMapping("/delete")
    @Transactional
    public R<Object> delete(){
        UserDo userDo = UserContext.getCurrentUser();
        userService.delete(userDo.getId());
        return R.ok("用户注销成功",null);
    }

    /**
     * 发送反馈邮件
     * @param feedback 反馈内容
     * @return 发送成功信息
     **/
    @PostMapping("/feedback")
    public R<Object> feedback(@RequestParam String feedback){
        UserDo userDo = UserContext.getCurrentUser();
        String email = userDo.getEmail();
        if(email.equals("default")){
            throw new BadRequestException("请先绑定邮箱");
        }
        try {
            FeedbackEmailBuilder builder = new FeedbackEmailBuilder(feedback);
            emailService.send(builder.build(email));
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException("邮件发送失败");
        }
        return R.ok("成功发送邮件",null);
    }

    /**
     * 获取用户收藏的话题列表
     * @return 话题列表
     */
    @GetMapping("/favorite/topics")
    public R<List<TopicDTO>> getFavoriteTopics() {
        String userId = UserContext.getCurrentUser().getId();
        log.info("获取用户收藏的话题列表, userId:{}", userId);
        List<TopicDTO> topics = scienceTopicService.getFavoriteTopics(userId);
        return R.ok("成功获取用户收藏的话题列表", topics);
    }

    // 获取新闻收藏 路径参数是userId 返回的是一个List<NewsDTO>

}
