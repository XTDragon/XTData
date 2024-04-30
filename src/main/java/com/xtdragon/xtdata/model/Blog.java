package com.xtdragon.xtdata.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String imgUrl;

    private String  imgFile;

    private String title;

    @JsonFormat(pattern = "yyyy年MM月dd日", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy年MM月dd日", timezone = "GMT+8")
    private Date lastModifiedTime;
    //    private LocalDateTime createTime;
    private int comments;

    private int lookTimes;

    private String content;

    private boolean enable;

    public Blog(String title, Date createTime, Date lastModifiedTime, int comments, int lookTimes, String content) {
        this.title = title;
        this.createTime = createTime;
        this.lastModifiedTime = lastModifiedTime;
        this.comments = comments;
        this.lookTimes = lookTimes;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                ", createTime=" + createTime +
                ", lastModifiedTime=" + lastModifiedTime +
                ", comments=" + comments +
                ", lookTimes=" + lookTimes +
                ", enable=" + enable +
                '}';
    }
}
