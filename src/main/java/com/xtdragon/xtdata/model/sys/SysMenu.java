package com.xtdragon.xtdata.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户菜单(SysMenu)实体类
 *
 * @author makejava
 * @since 2025-02-18 11:25:39
 */
@Data
public class SysMenu implements Serializable {
    private static final long serialVersionUID = -29964641737317659L;
    /**
     * 菜单项的唯一标识。
     */
    private Integer menuId;
    /**
     * 父菜单项的 ID，用于实现多级菜单。
     */
    private Integer parentId;
    /**
     * 菜单项名称。
     */
    private String name;
    /**
     * 菜单项图标（可选）
     */
    private String icon;
    /**
     * 菜单项对应的路由
     */
    private String route;
    /**
     * 子路由
     */
    @TableField(exist = false)
    private List<SysMenu> children;

}

