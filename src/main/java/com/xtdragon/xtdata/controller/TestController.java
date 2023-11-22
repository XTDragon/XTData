package com.xtdragon.xtdata.controller;

import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.service.PoetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    PoetryService poetryService;

    @Autowired
    Flow_qhoa_gsfwMapper flow_qhoa_gsfwMapper;

    @Autowired
    Flow_qhoa_gsfw_bakMapper flowQhoaGsfwBakMapper;

    @RequestMapping("/")
    public String helloWorld() {
        System.out.println("HelloWorld");
        return "<h1>HelloWorld</h1>";
    }

    //    @PostMapping("/user/login")
    public CommonResult login() {
        System.out.println();

        System.out.println("postuser");
        return CommonResult.success("/index", "cheng");
    }

    //    @GetMapping("/user/get")
    public String userGet() {
        System.out.println("HelloWorld");
        return "/index";
    }


    @RequestMapping("/test")
    public String test() {
        System.out.println("test");
        System.out.println(poetryService.getPoetry());
        return "/main";
    }

}
