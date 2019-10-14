package com.itheima.dao;

import com.itheima.pojo.OrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OrderSettingDao {
    /**
     * 通过日期查询预约信息
     * @param orderDate
     * @return
     */
    OrderSetting findByOrderDate(Date orderDate);

    /**
     * 更新可预约数量
     * @param osInDB
     */
    void updateNumber(OrderSetting osInDB);

    /**
     * 添加预约信息
     * @param orderSetting
     */
    void add(OrderSetting orderSetting);

    /**
     * 按日期范围查询
     * @param startDate
     * @param endDate
     * @return
     */
    List<OrderSetting> getOrderSettingBetweenDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    //更新可预约人数
    void editNumberByOrderDate(OrderSetting orderSetting);
    //更新已预约人数
    void editReservationsByOrderDate(OrderSetting orderSetting);
}
