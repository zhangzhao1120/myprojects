package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
/**
 * 体检预约的服务
 */
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Result order(Map map) throws Exception {
        //第1 先对用户提交的体检日期进行校验,看数据库中是否对这一天进行了预约设置
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);

        OrderSetting orderSetting = orderSettingDao.findOrderSettingByOrderDate(date);
        if (orderSetting == null) {
            //说明当天没有进行预约设置
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //第2 根据上一步查到的ordersetting获取当天预约人数是否已满
        int number = orderSetting.getNumber();//可预约人数
        int reservations = orderSetting.getReservations();//已预约人数
        if(reservations>=number){
            //说明预约已满
            return new Result(false, MessageConstant.ORDER_FULL);
        }
        //第3   判断是否同一个用户在同一天进行了同一个套餐的体检预约
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if(member!=null){
            //说明数据库里有该会员的信息
            Integer memberId = member.getId();
//            Integer setmealId = (Integer) map.get("setmealId");
            String setmealId = (String) map.get("setmealId");
            int setmealiddd = Integer.parseInt(setmealId);
            Order order=new Order(memberId,date,setmealiddd);
            List<Order> list = orderDao.findByCondition(order);
            if(list!=null&&list.size()>0){
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }else{
            //第4 说明数据库里没有该会员的信息,将这个人自动注册为会员
            member=new Member();
            String name = (String) map.get("name");
            member.setName(name);
            member.setPhoneNumber(telephone);
            String idCard = (String) map.get("idCard");
            member.setIdCard(idCard);
            String sex = (String) map.get("sex");
            member.setRegTime(new Date());
            memberDao.add(member);
        }
            //第5 更新ordersetting中的已预约的人数
            Order order=new Order();
            order.setMemberId(member.getId());
            order.setOrderDate(date);
            order.setOrderType((String) map.get("orderType"));
            order.setOrderStatus(Order.ORDERSTATUS_NO);
            String setmealId = (String) map.get("setmealId");
            int setmealhhh = Integer.parseInt(setmealId);
            order.setSetmealId(setmealhhh);
//            order.setSetmealId((Integer) map.get("setmealId"));
            orderDao.add(order);
            orderSetting.setReservations(orderSetting.getReservations()+1);
           orderSettingDao.editReservationsByOrderDate(orderSetting);
            return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if(map!=null){
            Date orderDate = (Date) map.get("orderDate");
//            String orderDate1 = (String) map.get("orderDate");
//            Date orderDate = DateUtils.parseString2Date(orderDate1);
            map.put("orderDate", DateUtils.parseDate2String(orderDate));
        }

        return map;
    }
}
