package com.xtdragon.xtdata.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.model.User;
import com.xtdragon.xtdata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserService userService;

    // 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=root&password=123456
    @RequestMapping("doLogin")
    public CommonResult doLogin(@RequestBody User user) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        User data = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (data != null && user.getUsername().equals(data.getUsername())
                && user.getPassword().equals(data.getPassword())) {
            StpUtil.login(user.getUsername());
            return CommonResult.success(user, "登录成功");
        }
        return CommonResult.failed("登录失败");
    }

    // 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
    @RequestMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }


    @RequestMapping("/UserList")
//    @SaCheckLogin
    public CommonResult getUserList() {
        return CommonResult.success(userService.list());
    }

}
