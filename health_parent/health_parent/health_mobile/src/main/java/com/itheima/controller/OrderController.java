package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;
    
    @PostMapping("/submit")
    public Result submitOrder(@RequestBody Map<String,String> map){
        Jedis jedis = jedisPool.getResource();
        // 验证码有效性
        String telephone = map.get("telephone");
        String key = "Order_" + RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        // redis中的验证码
        String codeInRedis = jedis.get(key);
        if(StringUtils.isEmpty(codeInRedis)){
            // 空值, 过时或没有点击获取验证码
            return new Result(false, "请点击获取验证码");
        }
        // 编码规范：先把不符合条件的先处理，后再处理符合条件。
        // 有值
        if(!codeInRedis.equals(map.get("validateCode"))){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 清除redis中的验证码
        jedis.del(key);
        // 调用业务服务
        map.put("orderType","微信预约");
        Order order = orderService.submitOrder(map);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order);
    }

    @GetMapping("/findById")
    public Result findById(int id){
        // 调用业务查询
        Map<String,Object> data = orderService.findById(id);
        return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, data);
    }
}
