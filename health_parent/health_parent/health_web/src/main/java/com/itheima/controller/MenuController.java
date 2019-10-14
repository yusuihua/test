package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Menu;
import com.itheima.service.MenuService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Reference
    private MenuService menuService;

    @RequestMapping("/getRoleMenu")
    public Result getRoleMenu(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Menu> menuList = menuService.getRoleMenu(name);
        return new Result(true, MessageConstant.GET_MENU_SUCCESS,menuList);
    }
}
