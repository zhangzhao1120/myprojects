package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Reference
    private SetMealService setMealService;
    //用户手机短信快速登录
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        //生成一个随机的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        JedisPool jedisPool=new JedisPool();
        Jedis jedis = jedisPool.getResource();
        try{
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println(validateCode);
            //如果发送短信验证码成功,将这个验证码保存在redis中,用户输入时用来校验
            jedis.setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,600,validateCode.toString());
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }


        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

   //接收前端穿过 来的电话号码,生成验证码,返回给客户,并且把生成的验证码保存到redis中,设置一个过期时间
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        //生成一个随机的验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        JedisPool jedisPool=new JedisPool();
        Jedis jedis = jedisPool.getResource();
        try{
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
            System.out.println(validateCode);
            //如果发送短信验证码成功,将这个验证码保存在redis中,用户输入时用来校验
            jedis.setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,600,validateCode.toString());
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }


        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
