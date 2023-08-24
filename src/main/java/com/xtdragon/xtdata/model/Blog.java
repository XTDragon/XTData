package com.xtdragon.xtdata.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Blog {
    private Integer id;
    private String imgUrl;
    private String title;
    private LocalDateTime createTime;
    private int comments;
    private String content;
}
