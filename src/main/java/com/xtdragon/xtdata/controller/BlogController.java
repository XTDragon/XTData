package com.xtdragon.xtdata.controller;

import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.service.BlogService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlogController {

    final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }


    @RequestMapping("/get/bloglist")
//    @SaCheckLogin
    public CommonResult getBlogList() {
        blogService.list();
        return CommonResult.success(blogService.list());
    }

    @RequestMapping("/blog/{id}")
    public CommonResult getBlogById(@PathVariable String id) {
//        System.out.println(blogService.list());
        Blog blog = blogService.getById(id);
        blog.setLookTimes(blog.getLookTimes() + 1);
        blogService.updateById(blog);
        return CommonResult.success(blogService.getById(id));
    }

}
