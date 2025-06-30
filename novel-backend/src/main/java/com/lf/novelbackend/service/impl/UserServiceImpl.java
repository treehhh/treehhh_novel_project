package com.lf.novelbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lf.novelbackend.model.entity.User;
import com.lf.novelbackend.service.UserService;
import com.lf.novelbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 86138
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-06-30 17:48:06
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




