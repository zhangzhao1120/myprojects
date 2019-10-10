package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Override
    public void add(List<OrderSetting> list) {
        //先对集合不为空进行判断,再进行遍历
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                //遍历 后先对当前日期是否进行了上传设置进行判断
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(count>0){
                    //说明当前日期已经被设置过一次了,需要进行更新操作
                    orderSettingDao.editNumByOrderDate(orderSetting);
                }else{
                    //说明当前日期还没有进行上传设置,进行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }
//查询数据库中的预约设置信息
    @Override
    public List<Map> getOrderSettingByDate(String date) {
        String begin=date+"-1";
        String end=date+"-31";
        Map<String,String>map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        List<OrderSetting>list=orderSettingDao.getOrderSettingByDate(map);
        List<Map>result=new ArrayList<>();
        if(list!=null&&list.size()>0){
            for (OrderSetting orderSetting : list) {
                Map<String,Object> m=new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//获取日期数字的日
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }

        }


        return result;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        Date orderDate = orderSetting.getOrderDate();
        long countByOrderDate = orderSettingDao.findCountByOrderDate(orderDate);
        if(countByOrderDate>0){
            //说明数据库中已经有这一天的预约设置,所以需要执行修改数据的 操作
            orderSettingDao.editNumByOrderDate(orderSetting);
        }else{
            //说明数据库中对这一天的预约设置还没有进行设置,所以需要执行 增加数据 操作
            orderSettingDao.add(orderSetting);
        }
    }
}
