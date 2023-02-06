package com.xtdragon.xtdata.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
//@RequestMapping(value = "api")
public class FileController {
	
	@Value("${filePath}")
	private String filePath;

    @RequestMapping(value = "/downloadFile/{fileName}", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response, @PathVariable("fileName") String fileName){
//        File file = new File(filePath + '/' + fileName);
        File file = new File(filePath + "//downloadFile//" + fileName);
        System.out.println(file);
        if(!file.exists()){
            System.out.println("未找到此文件");
            return;
        }
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );
//在方法上添加跨域注解@CrossOrigin
//然后设置如下响应头，其中星号可以换成具体的域名
        response.addHeader("Access-Control-Allow-Origin","*");

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[2048];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return;
        }
       return;
    }

}