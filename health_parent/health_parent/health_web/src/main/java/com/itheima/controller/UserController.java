package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/getUsername")
    public Result getUsername(){
        // SecurityContextHolder, 持有整个security所有的信息（配置，登陆用户的权限)
        // getAuthentication() 获取认证信息->登陆用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        // 主角，主事，登陆用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user.getUsername());
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, username);
    }
}
