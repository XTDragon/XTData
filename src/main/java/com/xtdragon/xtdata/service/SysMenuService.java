package com.xtdragon.xtdata.service;

import com.xtdragon.xtdata.model.sys.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Gtja
* @description 针对表【sys_menu(用户菜单)】的数据库操作Service
* @createDate 2025-02-18 11:22:59
*/
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getMenusByCurrentUser();
}
