package com.xtdragon.xtdata.controller;

import com.xtdragon.xtdata.service.PoetryService;
import com.xtdragon.xtdata.utils.CommonResult;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    PoetryService poetryService;

    @RequestMapping("/")
    public String helloWorld() {
        System.out.println("HelloWorld");
        return "HelloWorld";
    }

    @PostMapping("/user/login")
    public CommonResult login() {
        System.out.println();

        System.out.println("postuser");
        return CommonResult.success("/index", "cheng");
    }

    @GetMapping("/user/get")
    public String userGet() {
        System.out.println("HelloWorld");
        return "/index";
    }


    @RequestMapping("/test")
    public String test(){
        System.out.println("test");
        System.out.println(poetryService.getPoetry());
        return "/main";
    }
}
