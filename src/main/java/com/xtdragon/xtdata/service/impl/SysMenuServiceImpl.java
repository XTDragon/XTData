package com.xtdragon.xtdata.service.impl;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtdragon.xtdata.model.sys.SysMenu;
import com.xtdragon.xtdata.service.SysMenuService;
import com.xtdragon.xtdata.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Gtja
 * @description 针对表【sys_menu(用户菜单)】的数据库操作Service实现
 * @createDate 2025-02-18 11:22:59
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {


    @Override
    public List<SysMenu> getMenusByCurrentUser() {
        if (!StpUtil.isLogin()) {
            return null;
        }

        StpUtil.getLoginIdAsString();
        return null;
    }
}




