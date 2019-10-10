package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
   public void add(List<OrderSetting>list);

    List<Map> getOrderSettingByDate(String date);

    void editNumberByDate(OrderSetting orderSetting);
}
