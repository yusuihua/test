package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.util.SMSUtils;
import com.itheima.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        // 获取redis操作对象，用完了要关闭
        Jedis jedis = jedisPool.getResource();
        String key = "Order_" + RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        // 判断是否发送过了，是否存在key值
        if(null != jedis.get(key)){
            // redis有值，已经发送过了
            return new Result(true, MessageConstant.SENT_VALIDATECODE);
        }
        // 生成验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        // 发送验证
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code + "");
            // 存入redis
            // 1: key
            // 2: 有效时间，单位秒。多长时间后这个key值就会自动删除
            // 3: key对应的值
            jedis.setex(key,5*60, code+ "");
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }

    /**
     * 快速登陆的验证码
     * @param telephone
     * @return
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        // 获取redis操作对象，用完了要关闭
        Jedis jedis = jedisPool.getResource();
        String key = "login_" + RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        // 判断是否发送过了，是否存在key值
        if(null != jedis.get(key)){
            // redis有值，已经发送过了
            return new Result(true, MessageConstant.SENT_VALIDATECODE);
        }
        // 生成验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        // 发送验证
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code + "");
            // 存入redis
            // 1: key
            // 2: 有效时间，单位秒。多长时间后这个key值就会自动删除
            // 3: key对应的值
            jedis.setex(key,5*60, code+ "");
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }
}
