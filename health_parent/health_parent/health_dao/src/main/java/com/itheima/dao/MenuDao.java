package com.itheima.dao;

import com.itheima.pojo.Menu;

import java.util.List;
import java.util.Map;

public interface MenuDao {
    List<Menu> getMenuIcon(String loginUserName);

    List<Menu> getMenuChildren(Map<String, Object> childrenMap);
}
