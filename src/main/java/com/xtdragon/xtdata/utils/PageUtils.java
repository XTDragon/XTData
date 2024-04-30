package com.xtdragon.xtdata.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageUtils implements Serializable {

    private static final long serialVersionUID = 1L;
    //总记录数
    private int totalCount;
    //每页记录数
    private int pageSize;
    //总页数
    private int totalPage;
    //当前页数
    private int currPage;
    //列表数据
    private List<?> list;


}