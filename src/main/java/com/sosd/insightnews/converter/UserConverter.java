package com.sosd.insightnews.converter;


import com.sosd.insightnews.dao.entity.User;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.dto.UpdateUserDTO;
import com.sosd.insightnews.dto.UserDTO;

public class UserConverter {

    // Dto -> Do -> Entity

    public static UserDTO do2dto(UserDo userDo) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(userDo.getName());
        userDTO.setEmail(userDo.getEmail());
        userDTO.setAvatar(userDo.getAvatar());
        userDTO.setGender(userDo.getGender());
        userDTO.setProfile(userDo.getProfile());
        userDTO.setRegion(userDo.getRegion());

        return userDTO;
    }

    public static User do2e(UserDo userDo) {
        User user = new User();
        user.setId(userDo.getId());
        user.setPhone(userDo.getPhone());
        user.setEmail(userDo.getEmail());
        user.setName(userDo.getName());
        user.setAvatar(userDo.getAvatar());
        user.setGender(userDo.getGender());
        user.setRegion(userDo.getRegion());
        user.setProfile(userDo.getProfile());
        user.setOpenId(userDo.getOpenid());
        // shouldn't set updateTime
        // user.setUpdateTime(userDo.getUpdateTime());
        return user;
    }

    public static UserDo e2do(User user) {
        UserDo userDo = new UserDo();
        userDo.setId(user.getId());
        userDo.setName(user.getName());
        userDo.setEmail(user.getEmail());
        userDo.setAvatar(user.getAvatar());
        userDo.setRegion(user.getRegion());
        userDo.setProfile(user.getProfile());
        userDo.setGender(user.getGender());
        userDo.setOpenid(user.getOpenId());
        userDo.setUpdateTime(user.getUpdateTime());
        return userDo;
    }

    public static UserDo dto2do(UserDTO UserDTO) {
        UserDo userDo = new UserDo();
        userDo.setName(UserDTO.getName());
        userDo.setAvatar(UserDTO.getAvatar());
        userDo.setGender(UserDTO.getGender());
        userDo.setEmail(UserDTO.getEmail());
        userDo.setProfile(UserDTO.getProfile());
        userDo.setRegion(UserDTO.getRegion());
        return userDo;
    }

    public static UserDo dto2do(UpdateUserDTO updateUserDTO) {
        UserDo userDo = new UserDo();
        userDo.setName(updateUserDTO.getName());
        userDo.setGender(updateUserDTO.getGender());
        userDo.setRegion(updateUserDTO.getRegion());
        userDo.setEmail(updateUserDTO.getEmail());
        userDo.setProfile(updateUserDTO.getProfile());
        userDo.setAvatar(updateUserDTO.getAvatar());
        return userDo;
    }
}
