package com.xtdragon.xtdata.controller;

import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.service.PoetryService;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    PoetryService poetryService;

    @Autowired
    XwikiSchedule xwikiSchedule;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/")
    public String helloWorld() {
        List<Blog> list=new ArrayList<>();
        Blog blog=new Blog();
        blog.setImgUrl("null");
        System.out.println(blog.getImgUrl());
        System.out.println("HelloWorld");
        return "<h1>HelloWorld</h1>";
    }

    //    @PostMapping("/user/login")
    public CommonResult login() {
        System.out.println("postuser");
        return CommonResult.success("/index", "cheng");
    }

    //    @GetMapping("/user/get")
    public String userGet() {
        System.out.println("HelloWorld");
        return "/index";
    }

//    @RequestMapping("/test")
    public void test() throws IOException {
        String url = "https://wiki.gtjaqh.net/bin/upload/%E4%BF%A1%E6%81%AF%E6%8A%80%E6%9C%AF/%E5%BC%80%E5%8F%91%E5%9B%A2%E9%98%9F/test/WebHome";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        Header header = new BasicHeader("Cookie", "JSESSIONID=473460EA15BEE9E5182EB4B8D9B07395");
//        HttpEntity httpEntity = new HttpEntity(null, httpHeaders);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addBinaryBody("filepath", new File("C:\\Users\\gtja_1\\Postman\\files\\test.txt"), ContentType.parse("text/plain"), "国泰君安期货产品手册V1.pdf");
//        builder.setMode(HttpMultipartMode.RFC6532);
        builder.setBoundary("---------------------------29064760291843756171430728010");
//        builder.addBinaryBody("filepath", new File("C:\\Users\\gtja_1\\Postman\\files\\国泰君安期货产品手册V1.pdf"));
//        InputStream inputStream=new FileInputStream("C:\\Users\\gtja_1\\Postman\\files\\国泰君安期货产品手册V1.pdf");
        builder.addBinaryBody("filepath",Files.readAllBytes(Paths.get("C:\\Users\\gtja_1\\Postman\\files\\test.txt")), ContentType.parse("text/plain"),"text.txt");
//        builder.addBinaryBody("filepath",inputStream);
        builder.addPart("xredirect",new StringBody("/bin/get/%E4%BF%A1%E6%81%AF%E6%8A%80%E6%9C%AF/%E5%BC%80%E5%8F%91%E5%9B%A2%E9%98%9F/test/?xpage=attachmentslist&forceTestRights=1"));
        builder.addPart("form_token",new StringBody("ei3WBek9dBYsvv5qUo68gg"));
        org.apache.http.HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        httpPost.addHeader(header);
        HttpResponse response = httpClient.execute(httpPost);

        System.out.println(response);

    }

    @RequestMapping("/test")
    public void testPush() {
        xwikiSchedule.pushPagefromDir("","/xwiki","");
    }

}
