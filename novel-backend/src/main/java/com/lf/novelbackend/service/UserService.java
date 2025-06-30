package com.lf.novelbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lf.novelbackend.model.dto.user.UserLoginRequest;
import com.lf.novelbackend.model.dto.user.UserQueryRequest;
import com.lf.novelbackend.model.dto.user.UserRegisterRequest;
import com.lf.novelbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lf.novelbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 86138
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-06-30 17:48:06
 */
public interface UserService extends IService<User> {

    long userRegister(UserRegisterRequest userRegisterRequest);

    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User user);

    boolean userLogout(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
