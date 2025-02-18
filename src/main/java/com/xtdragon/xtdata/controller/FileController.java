package com.xtdragon.xtdata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.mapper.BlogMapper;
import com.xtdragon.xtdata.mapper.GameFileMapper;
import com.xtdragon.xtdata.model.FCGameFile;
import com.xtdragon.xtdata.model.file.FileType;
import com.xtdragon.xtdata.model.file.TBaseFile;
import com.xtdragon.xtdata.service.TBaseFileService;
import com.xtdragon.xtdata.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;

@RestController
//@RequestMapping(value = "api")
@Slf4j
public class FileController {

    @Value("${xtdata.file.directory}")
    private String filePath;

    @Value("${xtdata.file.directory}")
    String baseFilePath;
    final RestTemplate restTemplate;

    final GameFileMapper gameFileMapper;

    final BlogMapper blogMapper;

    final TBaseFileService baseFileService;

    final MinioUtil minioUtil;

    public FileController(RestTemplate restTemplate, BlogMapper blogMapper, GameFileMapper gameFileMapper, TBaseFileService baseFileService, MinioUtil minioUtil) {
        this.restTemplate = restTemplate;
        this.blogMapper = blogMapper;
        this.gameFileMapper = gameFileMapper;
        this.baseFileService = baseFileService;
        this.minioUtil = minioUtil;
    }

    @RequestMapping("/upload")
    public CommonResult uploadVideo(@RequestParam String fileName, @RequestParam MultipartFile multipartFile) {
        // 文件存储目录
        String fileUrl = baseFilePath + "//" + FileType.filePath(multipartFile.getContentType()) + "//" + fileName;
        try {
            // 获取上传文件的MD5值
            minioUtil.upload(multipartFile,"video");
            String md5 = DigestUtils.md5Hex(multipartFile.getBytes());
            TBaseFile targetFileMD5 = baseFileService.getOne(new QueryWrapper<TBaseFile>().eq("md5", md5));
            if (Objects.isNull(targetFileMD5)) {
                // 将文件保存在服务器目录中
                File targetFile = new File(fileUrl);
                FileUtils.writeByteArrayToFile(targetFile, multipartFile.getBytes());
                baseFileService.save(new TBaseFile(multipartFile, md5, "//" + FileType.filePath(multipartFile.getContentType()) + "//" + fileName));
                log.info("文件上传成功:" +fileName);
            } else {
                log.info("文件重复:" + fileName);
            }
        } catch (Exception e) {
            log.error("保存文件到服务器失败:" + fileName, e);
        }
        return CommonResult.success();
    }


    @RequestMapping(value = "/downLoadFile/{id}")
    public void downLoadFile(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        System.out.println(id);
//        id= URLEncoder.encode(id,"ISO-8859-1")
//        QueryWrapper<FCGameFile> queryWrapper = new QueryWrapper();
        FCGameFile fcGameFile = gameFileMapper.selectById(id);

        response.reset();
        response.setCharacterEncoding("utf-8");
//        response.setContentLength((int) fcGameFile.getGameSize());
        /**一定要设置成xlsx格式*/
        response.setHeader("Content-Disposition", "attachment;filename=" + fcGameFile.getGameName());
        /**创建一个输出流*/
        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(fcGameFile.getGameFile());
        /**关闭流*/
        outputStream.close();
    }

    @RequestMapping(value = "/GameList", method = RequestMethod.GET)
    public CommonResult getGameList() {
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
        return CommonResult.success(gameIPage.getRecords());
    }

    @RequestMapping(value = "/Search", method = RequestMethod.GET)
    public CommonResult SearchGame(String input) {
        if (input.equals("")) {
            IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
            return CommonResult.success(gameIPage.getRecords());
        }
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), new QueryWrapper<FCGameFile>().eq("game_name", input));
        return CommonResult.success(gameIPage.getRecords());
    }

}