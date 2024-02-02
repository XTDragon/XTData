package com.xtdragon.xtdata.model;

import lombok.Data;

import java.util.List;

/**
 * 菜单类
 */
@Data
public class Menu {
    public Integer id;
    public String name;
    public Integer parentId;
    public List<Menu> childList;

    public Menu(Integer id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

}