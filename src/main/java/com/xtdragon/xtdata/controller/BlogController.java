package com.xtdragon.xtdata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.DataTypeUtil;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.dao.BlogMapper;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.service.BlogService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@RestController
public class BlogController {

    final BlogService blogService;

    final RestTemplate restTemplate;

    final BlogMapper blogMapper;
    public BlogController(BlogService blogService, RestTemplate restTemplate, BlogMapper blogMapper) {
        this.blogService = blogService;
        this.restTemplate = restTemplate;
        this.blogMapper = blogMapper;
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

    @Scheduled(cron = "0 */10 * * * *")
    @RequestMapping(value = "/docAutoSynced")
    public void documentAutoSynced() {
        String path = "C:\\Users\\gtja_1\\OneDrive\\文档";
        File docDir = new File(path);
        try {
            for (File file : Objects.requireNonNull(docDir.listFiles())) {
                Blog blog = blogMapper.selectOne(new QueryWrapper<Blog>().eq("title", file.getName().substring(0, file.getName().lastIndexOf("."))));
                if (blog != null) {
                    Date lastModifiedTime = new Date(file.lastModified());
                    if (blog.getLastModifiedTime().getTime() < lastModifiedTime.getTime()) {
                        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        Date creationTime = new Date(basicFileAttributes.creationTime().toMillis());
                        StringBuilder result = new StringBuilder();
                        // 构造一个BufferedReader类来读取文件
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String string = null;
                        // 使用readLine方法，一次读一行
                        while ((string = br.readLine()) != null) {
                            result.append(System.lineSeparator()).append(string);
                        }
                        br.close();
                        String fileName = file.getName();
                        Blog newblog = new Blog(fileName.substring(0, fileName.lastIndexOf(".")), creationTime, lastModifiedTime, 0, 0, result.toString());

                        blogMapper.update(newblog, new QueryWrapper<Blog>().eq("id", blog.getId()));
                    }
                } else {
                    Date lastModifiedTime = new Date(file.lastModified());
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    Date creationTime = new Date(basicFileAttributes.creationTime().toMillis());
                    StringBuilder result = new StringBuilder();
                    // 构造一个BufferedReader类来读取文件
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String string = null;
                    // 使用readLine方法，一次读一行
                    while ((string = br.readLine()) != null) {
                        result.append(System.lineSeparator()).append(string);
                    }
                    br.close();
                    ResponseEntity<String> response = restTemplate.exchange("https://moey.cn/wallpaper/?type=josn&form=level", HttpMethod.GET,null,String.class);
                    String fileName = file.getName();
                    Blog newblog = new Blog(fileName.substring(0, fileName.lastIndexOf(".")), creationTime, lastModifiedTime, 0, 0, result.toString());
                    newblog.setImgUrl(response.getBody());
                    ResponseEntity<Resource>  imgFile = restTemplate.exchange(newblog.getImgUrl(), HttpMethod.GET, null, Resource.class);
//                    int read = imgFile.getBody().getInputStream().read();



                    newblog.setImgFile(imgFile.getBody().getInputStream().readAllBytes());
//                  System.out.println(response);
                    blogMapper.insert(newblog);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
