package com.xtdragon.xtdata.model.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

import java.util.Date;


/**
 * @TableName t_base_file
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TBaseFile implements Serializable {

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件md5
     */
    private String md5;
    /**
     * 上传时间
     */
    private Date creatTime;
    /**
     * 文件存储路径
     */
    private String filePath;

    public TBaseFile(MultipartFile multipartFile, String md5 ,String filePath) {
        this.fileName = multipartFile.getOriginalFilename();
        this.fileSize = multipartFile.getSize();
        this.fileType = multipartFile.getContentType();
        this.md5 = md5;
        this.filePath =filePath;
    }
}
