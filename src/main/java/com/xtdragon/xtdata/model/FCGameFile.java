package com.xtdragon.xtdata.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@TableName("t_data_game")
public class FCGameFile {

    @TableId
    private Integer gameId;

    @TableField("game_name")
    private String gameName;

    @TableField("game_size")
    private double gameSize;

    @TableField("game_file")
    private byte[] gameFile;

}
