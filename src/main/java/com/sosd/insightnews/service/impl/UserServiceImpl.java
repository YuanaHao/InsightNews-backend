package com.sosd.insightnews.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.converter.PermissionConverter;
import com.sosd.insightnews.converter.RoleConverter;
import com.sosd.insightnews.converter.UserConverter;
import com.sosd.insightnews.dao.entity.*;
import com.sosd.insightnews.dao.mapper.*;
import com.sosd.insightnews.domain.RoleDo;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.dto.LoginDTO;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.RoleService;
import com.sosd.insightnews.service.UserService;
import com.sosd.insightnews.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sosd.insightnews.constant.RedisConstants.VERIFY_CODE;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDo getUserById(String id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        UserDo domain = UserConverter.e2do(user);

        List<Role> rolesByUserId = getRolesByUserId(id);
        if (rolesByUserId == null) {
            return domain;
        }
        domain.setRoles(RoleConverter.role2RoleDo(rolesByUserId));

        List<String> roleIds = domain.getRoles().stream().map(RoleDo::getRoleid).toList();
        List<Permission> permissionsByRoleIds = getPermissionsByRoleIds(roleIds);
        if (permissionsByRoleIds == null) {
            return domain;
        }
        domain.setPermissions(PermissionConverter.permission2PermissionDo(permissionsByRoleIds));
        return domain;
    }

    @Override
    @Transactional
    public void updateInfo(UserDo domain) {
        log.info("User[id={}] update domain={}", domain.getId(), domain);
        User e = UserConverter.do2e(domain);
        userMapper.update(e, prepareUpdate(e));
    }

    @Override
    public void logout() {
        UserContext.clear();
    }

    @Override
    @Transactional
    public String register(LoginDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();
        User user = userMapper.selectById(phone);
        if (user != null) {
            throw new BadRequestException("已存在用户，请重新登录", phone);
        }
        String redisCode = stringRedisTemplate.opsForValue().get(VERIFY_CODE + phone);
        if (redisCode == null) {
            throw new BadRequestException("验证码已过期", phone);
        }else if (!redisCode.equals(code)) {
            throw new BadRequestException("验证码错误", phone);
        }
        stringRedisTemplate.delete(VERIFY_CODE + phone);
        User newUser = new User();
        // use phone as id
        newUser.setId(phone);
        newUser.setPhone(phone);
        // set default avatar
        newUser.setAvatar("https://insightnews.oss-cn-hangzhou.aliyuncs.com/WechatIMG447.jpg");
        userMapper.insert(newUser);
        // init role: USER, UserSelf
        roleService.assignUserToUsers(newUser.getId());
        return JwtUtil.createTokenByUserId(newUser.getId());
    }

    @Override
    @Transactional
    public String login(LoginDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();
        User user = userMapper.selectById(phone);
        if (user == null) {
            throw new BadRequestException("用户不存在", phone);
        }
        String redisCode = stringRedisTemplate.opsForValue().get(VERIFY_CODE + phone);
        if (redisCode == null) {
            throw new BadRequestException("验证码已过期", phone);
        }else if (!redisCode.equals(code)) {
            throw new BadRequestException("验证码错误", phone);
        }
        stringRedisTemplate.delete(VERIFY_CODE + phone);
        UserDo userDo = UserConverter.e2do(user);
        return JwtUtil.createTokenByUserId(userDo.getId());
    }

    @Override
    @Transactional
    public void delete(String id) {
        userMapper.deleteById(id);
        List<Role> roles = getRolesByUserId(id);
        if (roles == null) {
            return;
        }
        //删除角色和权限
        for (Role role : roles) {
            roleService.deleteUserRoleByRoleId(role.getRoleid());
        }
    }

    // only update not null column
    private LambdaUpdateWrapper<User> prepareUpdate(User e) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(e.getName() != null, User::getName, e.getName())
                .set(e.getGender() != null, User::getGender, e.getGender())
                .set(e.getRegion()!= null, User::getRegion, e.getRegion())
                .set(e.getProfile()!= null, User::getProfile, e.getProfile())
                .set(e.getOpenId() != null, User::getOpenId, e.getOpenId())
                .set(e.getAvatar() != null, User::getAvatar, e.getAvatar())
                .eq(User::getId, e.getId());
        return wrapper;
    }

    private List<Role> getRolesByUserId(String userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(UserRole::getRoleid).eq(UserRole::getUserid, userId);
        List<String> roleIds = userRoleMapper.selectObjs(wrapper).stream().map(String::valueOf).toList();
        if (roleIds.isEmpty()) {
            log.info("No user roles found for user ID: {}", userId);
            return null;
        }
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Role::getRoleid, roleIds);
        return roleMapper.selectList(queryWrapper);
    }

    private List<Permission> getPermissionsByRoleIds(List<String> roleIds) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RolePermission::getRoleid, roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return null;
        }
        List<Integer> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionid).toList();
        return permissionMapper.selectBatchIds(permissionIds);
    }
}
