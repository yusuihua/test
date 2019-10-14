package com.itheima.service;

import com.itheima.exception.MyException;
import com.itheima.pojo.Order;

import java.util.Map;

public interface OrderService {
    /**
     * 预约体检
     * @param map
     * @return
     */
    Order submitOrder(Map<String,String> map) throws MyException;

    /**
     * 查询预约信息
     * @param id
     * @return
     */
    Map<String,Object> findById(int id);
}
