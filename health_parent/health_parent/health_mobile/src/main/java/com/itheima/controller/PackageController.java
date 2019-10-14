package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.util.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/package")
public class PackageController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private PackageService packageService;

    @GetMapping("getPackage")
    public Result getPackage(){
        String key = "packe_list";
        Jedis jedis = jedisPool.getResource();
        String dbKey = jedis.get(key);
        List<Package> list = null;
        if (dbKey == null) {
            // 查询所有的套餐列表
            list = packageService.findAll();
            // 拼接套餐图片的完整路径
            if(null != list){
                // 取出list中的每个元素赋值给pkg变量
                // pkg相当于list中的元素
                // 只要对pkg进行修改，list中的元素也会跟着修改
            /*for (Package pkg : list) {
                pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
            }*/
                list.forEach(pkg -> {
                    pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
                });
            }
            try {
                //将list转化为json
                String jsonStr = new ObjectMapper().writeValueAsString(list);
                jedis.set(key,jsonStr);
                return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS,list);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new Result(false,MessageConstant.GET_PACKAGE_COUNT_REPORT_FAIL);
            }
        }
        List<Object> list1 = JSON.parseArray(dbKey);
        List<Map<String,Package>> listw = new ArrayList<>();
        for (Object object : list1){
//            Map<String,Object> ageMap = new HashMap<>();
            Map <String,Package> ret = (Map<String, Package>) object;//取出list里面的值转为map
            listw.add(ret);
        }
        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS,listw);    }

    /**
     * 查询套餐详情
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(int id){
        String key = "packe_one"+id;
        Jedis jedis = jedisPool.getResource();
        String dbKey = jedis.get(key);
        Package pkg = null;
        if (dbKey == null) {
            // 调用业务服务查询套餐详情
            pkg = packageService.findById(id);
            // 拼接图片完整路径
            pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
            try {
                //将Package转化为json
                String jsonStr = new ObjectMapper().writeValueAsString(pkg);
                jedis.set(key,jsonStr);
                return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS,pkg);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new Result(false,MessageConstant.GET_PACKAGE_COUNT_REPORT_FAIL);
            }
        }
        Map<String,Object> map = (Map<String, Object>) JSON.parse(dbKey);
        jedis.close();
        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS,map);
    }

    /**
     * 查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/findPackageById")
    public Result findPackageById(int id){
        // 调用业务服务查询套餐详情
        Package pkg = packageService.findPackageById(id);
        // 拼接图片完整路径
        pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS,pkg);
    }
}
