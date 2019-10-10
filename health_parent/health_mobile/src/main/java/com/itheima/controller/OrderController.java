package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.OrderService;
import com.itheima.service.SetMealService;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;
    //客户预约成功后跳转的成功页面,需要展示客户的基本预约信息

    @RequestMapping("/findById")
    public Result findByID(Integer id){
        try {
            Map map=orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }

    }


    //根据套餐ID查询出该套餐的具体信息
    @RequestMapping("/submit")
    public Result findById(@RequestBody Map map) {
        //获取用户输入的手机号
        String telephone = (String) map.get("telephone");
        //先进行短信验证码的校验,从jedis中取出来
        String valicodeinredis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        Result result=null;
        if(valicodeinredis!=null&&validateCode!=null&&valicodeinredis.equals(validateCode)){
            //在map
           map.put("orderType", com.itheima.pojo.Order.ORDERTYPE_WEIXIN);
            //说明短信验证码校验通过,调用服务 去完成预约业务
            try {
                result = orderService.order(map);
            }catch (Exception e){
                e.printStackTrace();
                return result;
            }
            if(result.isFlag()){
                try {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone, (String) map.get("orderDate"));
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            //说明验证码校验失败,给用户对应的提示信息
            return  new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }

    }

}
