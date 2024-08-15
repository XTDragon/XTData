package com.xtdragon.xtdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtdragon.xtdata.dao.UserMapper;
import com.xtdragon.xtdata.model.User;
import com.xtdragon.xtdata.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {
}
