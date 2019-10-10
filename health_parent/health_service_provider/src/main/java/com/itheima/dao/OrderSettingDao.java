package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public void add(OrderSetting orderSetting);
    public void editNumByOrderDate(OrderSetting orderSetting);
    public long findCountByOrderDate(Date orderdate);

    public  List<OrderSetting> getOrderSettingByDate(Map map);

    OrderSetting findOrderSettingByOrderDate(Date date);
    public void editReservationsByOrderDate(OrderSetting orderSetting);
}
