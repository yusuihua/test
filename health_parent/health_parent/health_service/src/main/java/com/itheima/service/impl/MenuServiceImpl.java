package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MenuDao;
import com.itheima.pojo.Menu;
import com.itheima.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuDao menuDao;

    @Override
    public List<Menu> getRoleMenu(String loginUserName) {
        List<Menu> menuList = null;
        try {
            menuList = menuDao.getMenuIcon(loginUserName);
            if (menuList != null) {
                for (Menu menu : menuList) {
                    Map<String,Object> childrenMap = new HashMap<>();
                    childrenMap.put("name",loginUserName);
                    childrenMap.put("id",menu.getId());
                    List<Menu> childrenList = menuDao.getMenuChildren(childrenMap);
                    menu.setChildren(childrenList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menuList;
    }
}
