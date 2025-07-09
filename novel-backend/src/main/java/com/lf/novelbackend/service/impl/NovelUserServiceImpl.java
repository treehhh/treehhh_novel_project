package com.lf.novelbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lf.novelbackend.model.entity.NovelUser;
import com.lf.novelbackend.service.NovelUserService;
import com.lf.novelbackend.mapper.NovelUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author 86138
 * @description 针对表【novel_user(小说-用户关系表)】的数据库操作Service实现
 * @createDate 2025-07-09 21:09:07
 */
@Service
public class NovelUserServiceImpl extends ServiceImpl<NovelUserMapper, NovelUser>
        implements NovelUserService {

}




