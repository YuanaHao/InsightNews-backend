package com.sosd.insightnews.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sosd.insightnews.dao.entity.User;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.dto.LoginDTO;


public interface UserService extends IService<User> {

    UserDo getUserById(String userId);

    void updateInfo(UserDo userDoO);

    void logout();

    String login(LoginDTO loginDTO);

    void delete(String id);

    String register(LoginDTO loginDTO);
}
