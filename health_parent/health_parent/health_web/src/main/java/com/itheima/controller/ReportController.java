package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiContext;
import org.jxls.util.JxlsHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.nio.cs.ext.PCK;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        // 调用业务服务查询上过去一年的会员数量
        Map<String,List<Object>> data = memberService.getMemberReport();
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,data);
    }

    /**
     * 套餐占比
     * @return
     */
    @GetMapping("/getPackageReport")
    public Result getPackageReport(){
        // 查询套餐占比数据  Map<String,Object> value: 数量， name: 套餐名称， 每个套餐就是一个map
        List<Map<String,Object>> packageCount = reportService.getPackageReport();
        // 组装套餐列表数据
        List<String> packageNames = new ArrayList<String>();
        if(null != packageCount){
            for (Map<String, Object> map : packageCount) {
                //map 一个套餐数据
                packageNames.add((String) map.get("name"));
            }
        }
        Map<String,Object> result = new HashMap<String,Object>(2);
        result.put("packageNames",packageNames);
        result.put("packageCount",packageCount);
        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS, result);
    }

    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        Map<String,Object> result = reportService.getBusinessReportData();
        return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, result);
    }

    @GetMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest req, HttpServletResponse res){
        // 调用业务服务查询运营数据
        Map<String, Object> map = reportService.getBusinessReportData();
        // 写到excel中
        // 1. 获取模板 getRealPath: webapp
        String template = req.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
        // 2. 根据创建模板创建工作簿
        try (Workbook wb = new XSSFWorkbook(template);){
            // 3. 获取工作表
            Sheet sht = wb.getSheetAt(0);
            // 4. 获取行、单元格、在对应的位置写入内容
            sht.getRow(2).getCell(5).setCellValue(((String) map.get("reportDate")));
            // 会员数据
            sht.getRow(4).getCell(5).setCellValue(((Integer) map.get("todayNewMember")));
            sht.getRow(4).getCell(7).setCellValue(((Integer) map.get("totalMember")));
            sht.getRow(5).getCell(5).setCellValue(((Integer) map.get("thisWeekNewMember")));
            sht.getRow(5).getCell(7).setCellValue(((Integer) map.get("thisMonthNewMember")));
            // 预约数量
            sht.getRow(7).getCell(5).setCellValue(((Integer) map.get("todayOrderNumber")));
            sht.getRow(7).getCell(7).setCellValue(((Integer) map.get("todayVisitsNumber")));
            sht.getRow(8).getCell(5).setCellValue(((Integer) map.get("thisWeekOrderNumber")));
            sht.getRow(8).getCell(7).setCellValue(((Integer) map.get("thisWeekVisitsNumber")));
            sht.getRow(9).getCell(5).setCellValue(((Integer) map.get("thisMonthOrderNumber")));
            sht.getRow(9).getCell(7).setCellValue(((Integer) map.get("thisMonthVisitsNumber")));
            // 热门套餐
            int rowCnt = 12;
            List<Map<String, Object>> hotPackage = (List<Map<String, Object>>) map.get("hotPackage");
            if(null != hotPackage){
                for (Map<String, Object> pkgMap : hotPackage) {
                    sht.getRow(rowCnt).getCell(4).setCellValue(((String) pkgMap.get("name")));
                    sht.getRow(rowCnt).getCell(5).setCellValue(((Long) pkgMap.get("count")));
                    sht.getRow(rowCnt).getCell(6).setCellValue(((BigDecimal) pkgMap.get("proportion")).toString());
                    sht.getRow(rowCnt).getCell(7).setCellValue(((String) pkgMap.get("remark")));
                    rowCnt++;
                }
            }

            // 5. 调用Response的输出流实现 下载
            // 6. 告诉浏览器接收的是文件 Content-Type
            // 这个内容的是excel文件
            res.setContentType("application/vnd.ms-excel");
            // 下载文件
            String filename = "运营数据.xlsx";
            // 解决乱码
            filename = new String(filename.getBytes(),"ISO-8859-1");
            ServletOutputStream out = res.getOutputStream();
            res.setHeader("Content-Disposition","attachment;filename=" + filename);
            wb.write(out);
            out.flush();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
    }

    @GetMapping("/exportBusinessReport2")
    public void exportBusinessReport2(HttpServletRequest req, HttpServletResponse res){
        String template = req.getSession().getServletContext().getRealPath("template") + File.separator + "report_template2.xlsx";
        // 数据模型
        Context context = new PoiContext();
        context.putVar("obj", reportService.getBusinessReportData());
        try {
            res.setContentType("application/vnd.ms-excel");
            res.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            // 把数据模型中的数据填充到文件中
            JxlsHelper.getInstance().processTemplate(new FileInputStream(template),res.getOutputStream(),context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 会员性别占比统计
     * @return
     */
    @GetMapping("/getMemberGender")
    public Result getMemberGender(){
        List<Map<String,Object>> genderCount = reportService.getMemberGender();
        List<Object> gender = new ArrayList<>();
        if (null != genderCount) {
            for (Map<String, Object> map : genderCount) {
                gender.add((String)map.get("name"));
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("gender",gender);
        result.put("genderCount",genderCount);
        return new Result(true,MessageConstant.GET_GENDER_COUNT_REPORT_SUCCESS,result);
    }

    /**
     * 获取年龄占比报表
     * @return
     */
    @GetMapping("/getMemberAge")
    public Result getMemberAge(){
        List<Map<String,Object>> ageCount = reportService.getMemberAge();
        List<Object> age = new ArrayList<>();
        if (null != ageCount) {
            for (Map<String, Object> map : ageCount) {
                age.add((String)map.get("name"));
            }
        }
        Map<String,Object> result = new HashMap<>();
        result.put("age",age);
        result.put("ageCount",ageCount);
        return new Result(true,MessageConstant.GET_AGE_COUNT_REPORT_SUCCESS,result);
    }
}
