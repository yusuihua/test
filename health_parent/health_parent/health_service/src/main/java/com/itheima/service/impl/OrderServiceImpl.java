package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.exception.MyException;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * 预约体检
     * @param map
     * @return
     */
    @Transactional
    @Override
    public Order submitOrder(Map<String, String> map) throws MyException {
        //判断是否可以预约 通过日期来判断
        String orderDateStr = map.get("orderDate");
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        Date orderDate = null;
        try {
            orderDate = sdf.parse(orderDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            // orderDateStr有可能为空值，如果是空值进行拼接则会出nullpointException
            throw new MyException("日期转换失败: orderDateStr" + (orderDateStr==null?"":orderDateStr));
        }
        OrderSetting os = orderSettingDao.findByOrderDate(orderDate);
        if(null == os){
             throw new MyException(MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        if(os.getNumber() <= os.getReservations()){
            throw new MyException(MessageConstant.ORDER_FULL);
        }
        String telephone = map.get("telephone");
        //可预约, 判断是否为会员, 通过手机号码
        Member member = memberDao.findByTelephone(telephone);
        //不是会员，注册为会员，取出ID，将添加订单表时可以用
        if(null == member){
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setSex(map.get("sex"));
            member.setName(map.get("name"));
            member.setIdCard(map.get("idCard"));
            memberDao.add(member);
        }
        //是会员，取出它的ID，将添加订单表时可以用
        int memberId = member.getId();

        //判断是否已经预约过了，通过memeber_id，packageid, orderDate
        Order order = new Order();
        order.setMemberId(memberId);
        order.setPackageId(Integer.valueOf(map.get("packageId")));
        order.setOrderDate(orderDate);
        List<Order> orders = orderDao.findByCondition(order);
        //已预约，报错
        if(null != orders && orders.size() > 0){
            throw new MyException(MessageConstant.HAS_ORDERED);
        }
        //没预约则预约成功
        order.setOrderStatus("未到诊");
        order.setOrderType(map.get("orderType"));
        orderDao.add(order);
        //更新t_ordersetting已预约人数
        orderSettingDao.editReservationsByOrderDate(os);
        return order;
    }

    /**
     * 查询预约信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> findById(int id) {
        return orderDao.findById4Detail(id);
    }
}
