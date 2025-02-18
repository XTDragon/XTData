package com.xtdragon.xtdata.controller.mediacontroller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xtdragon.xtdata.common.CommonPage;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.mapper.BlogMapper;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
            minioUtil.upload(multipartFile);
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