package com.xtdragon.xtdata.controller.mediacontroller;

import cn.hutool.core.net.URLEncoder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xtdragon.xtdata.common.CommonPage;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.dao.BlogMapper;
import com.xtdragon.xtdata.model.file.FileType;
import com.xtdragon.xtdata.model.file.TBaseFile;
import com.xtdragon.xtdata.service.TBaseFileService;
import com.xtdragon.xtdata.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/video")
public class VideoController {

    @Value("${xtdata.file.directory}")
    String fileDirectory;


    final MinioUtil minioUtil;

    final RestTemplate restTemplate;

    final BlogMapper blogMapper;

    final TBaseFileService baseFileService;

    public VideoController(MinioUtil minioUtil, RestTemplate restTemplate, BlogMapper blogMapper, TBaseFileService baseFileService) {
        this.minioUtil = minioUtil;
        this.restTemplate = restTemplate;
        this.blogMapper = blogMapper;
        this.baseFileService = baseFileService;
    }


    @RequestMapping("/test")
    public void getVideoByFileName(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName) {
        String fileUrl = fileDirectory + "\\video\\" + fileName;
        //String fileName="C:\\Users\\gtja_1\\Desktop\\少年派\\少年派·第六期2023年11月2日.mp4";
        //视频资源存储信息
        response.reset();
        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
        log.info("获取视频资源:{},读取文件字节:{}", fileUrl, rangeString);
        try {
            //获取响应的输出流
            OutputStream outputStream = response.getOutputStream();
            File file = new File(fileUrl);
            if (file.exists()) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();
                //播放
                if (rangeString != null) {
                    long range = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                    //设置内容类型
                    response.setHeader("Content-Type", "video/mov");
                    //设置此次相应返回的数据长度
                    response.setHeader("Content-Length", String.valueOf(fileLength - range));
                    //设置此次相应返回的数据范围
                    response.setHeader("Content-Range", "bytes " + range + "-" + (fileLength - 1) + "/" + fileLength);
                    //返回码需要为206，而不是200
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    //设定文件读取开始位置（以字节为单位）
                    targetFile.seek(range);
                } else {
                    //下载
                    //设置响应头，把文件名字设置好
                    response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.createAll().encode(fileName, StandardCharsets.UTF_8));
                    //设置文件长度
                    response.setHeader("Content-Length", String.valueOf(fileLength));
                    //解决编码问题
                    response.setHeader("Content-Type", "application/octet-stream");
                }
                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache)) != -1) {
                    outputStream.write(cache, 0, flag);
                }
            } else {
                String message = "file:" + fileName + " not exists";
                //解决编码问题
                response.setHeader("Content-Type", "application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.info("获取视频资源出错:{},", fileUrl);
        }
    }


    @RequestMapping("/get/{id}")
    public void getVideo(HttpServletResponse response, @PathVariable("id") String id) {
        TBaseFile byId = baseFileService.getById(id);
        String fileUrl = fileDirectory + byId.getFilePath();
        //视频资源存储信息
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            minioUtil.download(response,byId.getFileName());
            outputStream = response.getOutputStream();
            fileInputStream = new FileInputStream(fileUrl);
            byte[] cache = new byte[1024];
            response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader(HttpHeaders.CONTENT_LENGTH, fileInputStream.available() + "");
            int flag;
            while ((flag = fileInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, flag);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("文件传输错误", e);
            throw new RuntimeException("文件传输错误");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("流释放错误", e);
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("文件流释放错误", e);
                }
            }
        }
    }

    @RequestMapping("/upload")
    public CommonResult uploadVideo(@RequestParam String fileName, @RequestParam MultipartFile multipartFile) {
        // 文件存储目录
        String fileUrl = fileDirectory + "\\video\\" + fileName;
        try {
            // 获取上传文件的MD5值
            String md5 = DigestUtils.md5Hex(multipartFile.getBytes());
            TBaseFile targetFileMD5 = baseFileService.getOne(new QueryWrapper<TBaseFile>().eq("md5", md5));
            if (Objects.isNull(targetFileMD5)) {
                // 将文件保存在服务器目录中
                File targetFile = new File(fileUrl);
                FileUtils.writeByteArrayToFile(targetFile, multipartFile.getBytes());
                baseFileService.save(new TBaseFile(multipartFile, md5, "\\video\\" + fileName));
            }
        } catch (IOException e) {
            log.error("保存文件到服务器失败:" + fileName, e);
        }
        return CommonResult.success();
    }

    @RequestMapping("/page")
    public CommonResult ListVideo(@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                  @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage) {
        List<String> collect = Arrays.stream(FileType.values())
                .filter(i -> i.getPath().equals("video"))
                .map(FileType::getType)
                .collect(Collectors.toList());
        CommonPage<TBaseFile> page= CommonPage.reset(
                baseFileService.page(new Page<>(),
                        new QueryWrapper<TBaseFile>().in("file_type", collect)));
        return CommonResult.success(page);
    }
}