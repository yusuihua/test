package com.itheima.service;

import com.itheima.pojo.User;

public interface UserService {
    /**
     * 认证与授权
     * @param username
     * @return
     */
    User findByUsername(String username);
}
