package com.xtdragon.xtdata.controller;

import cn.hutool.core.net.URLEncoder;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.service.PoetryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RestController
public class TestController {

    @Value("${xtdata.file.directory}")
    String fileDirectory;

    @Autowired
    PoetryService poetryService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/")
    public String helloWorld() {
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


    /**
     * 获取视频流
     * @param response
     * @return
     * @author xWang
     * @Date 2020-05-20
     */
    @RequestMapping("/getVideo")
    public void getVideo(HttpServletRequest request, HttpServletResponse response,@RequestParam String fileName) {
        String fileUrl = fileDirectory+"/video/"+fileName;
        //String fileName="C:\\Users\\gtja_1\\Desktop\\少年派\\少年派·第六期2023年11月2日.mp4";
        //视频资源存储信息
        response.reset();
        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
        log.info("getVideo获取视频资源:{},读取文件字节:{}",fileUrl,rangeString);
        try {
            //获取响应的输出流
            OutputStream outputStream = response.getOutputStream();
            File file = new File(fileUrl);
            if(file.exists()){
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();
                //播放
                if(rangeString != null){
                    long range = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                    //设置内容类型
                    response.setHeader("Content-Type", "video/mov");
                    //设置此次相应返回的数据长度
                    response.setHeader("Content-Length", String.valueOf(fileLength - range));
                    //设置此次相应返回的数据范围
                    response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
                    //返回码需要为206，而不是200
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    //设定文件读取开始位置（以字节为单位）
                    targetFile.seek(range);
                }else {
                    //下载
                    //设置响应头，把文件名字设置好
                    response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.createAll().encode(fileName,StandardCharsets.UTF_8) );
                    //设置文件长度
                    response.setHeader("Content-Length", String.valueOf(fileLength));
                    //解决编码问题
                    response.setHeader("Content-Type","application/octet-stream");
                }
                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache))!=-1){
                    outputStream.write(cache, 0, flag);
                }
            }else {
                String message = "file:"+fileName+" not exists";
                //解决编码问题
                response.setHeader("Content-Type","application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    File file=new File("C:\\Users\\gtja_1\\Desktop\\营销管理服务部");
    @RequestMapping("/test")
    public void test11() throws IOException {
        System.out.println(file.getName());
        test22(file,"营销管理服务部");
    }

    public void test22(File file,String url) throws IOException {

        for (File listFile : file.listFiles()) {
//            System.out.println(file.getName());
            if (listFile.isDirectory()){
                System.out.println(url+"-"+listFile.getName());
                test22(listFile,url+"-"+ listFile.getName());
            }
        }
    }

}
