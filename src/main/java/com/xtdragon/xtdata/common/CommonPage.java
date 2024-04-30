package com.xtdragon.xtdata.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xtdragon.xtdata.model.Blog;
import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 * Created by macro on 2019/4/19.
 */
@Data
public class CommonPage<T> {
    /**
     * 当前页码
     */
    private Long CurrentPage;
    /**
     * 每页数量
     */
    private Long pageSize;
    /**
     * 总页数
     */
    private Long totalPage;
    /**
     * 总条数
     */
    private Long total;
    /**
     * 分页数据
     */
    private List<T> list;


    public static <T> CommonPage<T> reset(IPage<T> iPage) {
        CommonPage<T> result = new CommonPage<>();
        result.setList(iPage.getRecords());
        result.setCurrentPage(iPage.getCurrent());
        result.setPageSize(iPage.getSize());
        result.setTotalPage(iPage.getPages());
        result.setTotal(iPage.getTotal());
        return result;
    }
}
