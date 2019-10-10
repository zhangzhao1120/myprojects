package com.itheima.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import com.itheima.service.SetMealService;
import com.itheima.utils.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 报表操作
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetMealService setMealService;
    @Reference
    private ReportService reportService;

    //会员数量折线图数据
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        Map<String, Object> map = new HashMap<>();
        List<String> months = new ArrayList();
        Calendar calendar = Calendar.getInstance();//获得日历对象，模拟时间就是当前时间
        //计算过去一年的12个月
        calendar.add(Calendar.MONTH, -12);//获得当前时间往前推12个月的时间
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);//获得当前时间往后推一个月日期
            Date date = calendar.getTime();
            months.add(new SimpleDateFormat("yyyy.MM").format(date));
        }
        map.put("months", months);

        List<Integer> memberCount = memberService.findMemberCountByMonths(months);
        map.put("memberCount", memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    //预约套餐占比饼形图
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        Map<String, Object> data = new HashMap<>();
        try {
            List<Map<String, Object>> setmealCount = setMealService.findSetmealCount();
            data.put("setmealCount", setmealCount);
            List<String> setmealNames = new ArrayList<>();
            for (Map<String, Object> map : setmealCount) {
                String name = (String) map.get("name");
                setmealNames.add(name);
            }
            data.put("setmealNames", setmealNames);
            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }

    //获取数据运营统计
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String, Object> data = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
    //导出统计 报表
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
       try{
           Map<String, Object> result = reportService.getBusinessReportData();
           TemplateExportParams params=new TemplateExportParams(request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template_easypoi.xlsx");
           Workbook workbook = ExcelExportUtil.exportExcel(params, result);
            OutputStream out = response.getOutputStream();
           response.setContentType("application/vnd.ms-excel");//代表的是Excel文件类型
           response.setHeader("content-Disposition", "attachment;filename=report.xlsx");//指定以附件形式进行下载
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            return null;
       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"导出数据失败");
       }

    }
}

