package com.itheima.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String,Object>> getPackageReport();

    Map<String,Object> getBusinessReportData();

    List<Map<String,Object>> getMemberGender();

    List<Map<String,Object>> getMemberAge();

}
