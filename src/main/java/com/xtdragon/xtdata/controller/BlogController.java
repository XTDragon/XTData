package com.xtdragon.xtdata.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogController {

    @Autowired
    BlogService blogService;


    @RequestMapping("/get/bloglist")
//    @SaCheckLogin
    public CommonResult getBlogList(){
//        System.out.println(blogService.list());
        return CommonResult.success(blogService.list());
    }

    @RequestMapping("/blog/{id}")
    public CommonResult  getBlogById(@PathVariable String id){
//        System.out.println(blogService.list());
        return CommonResult.success(blogService.getById(id));
    }
}
