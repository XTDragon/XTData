package com.xtdragon.xtdata.utils;

import com.xtdragon.xtdata.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;


    /**
     * 查看存储bucket是否存在
     *
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 获取全部bucket
     */
    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public boolean upload(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
            log.info("文件上传成功bucket:{},OriginalFilename:{}", minioConfig.getBucketName(), file.getOriginalFilename());
            return true;
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件上传失败bucket:{},OriginalFilename:{}", minioConfig.getBucketName(), file.getOriginalFilename());
            log.error(e.getMessage(), e);
            return false;
        }
    }


    public boolean upload(MultipartFile file, String path) {
        try {
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(path + "/" + file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("文件上传成功bucket:{},OriginalFilename:{}", minioConfig.getBucketName(), file.getOriginalFilename());
            return true;
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件上传失败bucket:{},OriginalFilename:{}", minioConfig.getBucketName(), file.getOriginalFilename());
            log.error(e.getMessage(), e);
            return false;
        }
    }


    public void download(HttpServletResponse response, String fileName) {
        InputStream in = null;
        try {
            // 获取对象信息
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build());
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 文件下载
            in = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error("文件下载失败bucket:{},filename:{}", minioConfig.getBucketName(), fileName);
            log.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
