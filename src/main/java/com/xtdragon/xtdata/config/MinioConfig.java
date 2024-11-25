package com.xtdragon.xtdata.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Data
@ConfigurationProperties(prefix = "minio")
public class MinioConfig implements WebMvcConfigurer {
    // MinIO地址
    private String endpoint;
    // MinIO accessKey
    private String accessKey;
    // MinIO secretKey
    private String secretKey;
    // MiniO桶名称
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
