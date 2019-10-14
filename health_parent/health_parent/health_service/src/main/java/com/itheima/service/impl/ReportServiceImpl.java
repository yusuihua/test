package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.PackageDao;
import com.itheima.service.ReportService;
import com.itheima.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itheima.util.DateUtils.YMD;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private PackageDao packageDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<Map<String, Object>> getPackageReport() {
        return packageDao.getPackageReport();
    }

    @Override
    public Map<String, Object> getBusinessReportData() {
        // 当前日期
        Date now = new Date();
        String reportDate = DateUtils.date2String(now, YMD);
        // 星期一的日期
        String monday = DateUtils.date2String(DateUtils.getThisWeekMonday(), YMD);
        // 星期天
        String sunday = DateUtils.date2String(DateUtils.getSundayOfThisWeek(), DateUtils.YMD);
        // 1号的日期
        String firstDayOfThisMonth = DateUtils.date2String(DateUtils.getFirstDayOfThisMonth(), YMD);
        // 最后一天
        String lastDayOfThisMonth = DateUtils.date2String(DateUtils.getLastDayOfThisMonth(), YMD);


        //********************************* 会员数据统计 *******************************
        //今日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(reportDate);
        // 总会员数
        Integer totalMember = memberDao.findMemberTotalCount();

        // 本周新增会员数
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);

        //本月新增会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDayOfThisMonth);

        //****************************** 预约到诊数据统计 *****************************
        // 今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(reportDate);
        // 今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(reportDate);
        // 本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountBetweenDate(monday,sunday);
        // 本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(monday);
        // 本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(firstDayOfThisMonth, lastDayOfThisMonth);
        // 本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDayOfThisMonth);

        //***************************** 热门套餐,取前4个 *****************************
        List<Map<String,Object>> hotPackage = orderDao.findHotPackage();

        Map<String, Object> result = new HashMap<String,Object>();
        result.put("reportDate",reportDate);
        result.put("todayNewMember",todayNewMember);
        result.put("totalMember",totalMember);
        result.put("thisWeekNewMember",thisWeekNewMember);
        result.put("thisMonthNewMember",thisMonthNewMember);
        result.put("todayOrderNumber",todayOrderNumber);
        result.put("todayVisitsNumber",todayVisitsNumber);
        result.put("thisWeekOrderNumber",thisWeekOrderNumber);
        result.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        result.put("thisMonthOrderNumber",thisMonthOrderNumber);
        result.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        result.put("hotPackage",hotPackage);
        return result;
    }

    @Override
    public List<Map<String, Object>> getMemberGender() {
        return memberDao.getMemberGender();
    }

    @Override
    public List<Map<String, Object>> getMemberAge() {
        return memberDao.getMemberAge();
    }
}
