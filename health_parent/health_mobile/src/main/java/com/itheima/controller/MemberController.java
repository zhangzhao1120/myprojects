package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;
    //客户预约成功后跳转的成功页面,需要展示客户的基本预约信息

    @RequestMapping("/login")
    public Result findByID(HttpServletResponse response, @RequestBody Map map) {
        //先获取 用户提交的手机号验证码数据,这里用map进行封装
        //第1   先对用户提交的验证码和在redis中保存的验证码进行比对
        String telephone = (String) map.get("telephone");//用户输入的手机号
        String validateCode = (String) map.get("validateCode");//用户输入的验证码
        String valiCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);//redis中保存的验证码
        if (validateCode != null && valiCodeInRedis != null && validateCode.equals(valiCodeInRedis)) {
            //第2 说明验证码输入正确,需要判断当前用户是否为会员,如果不是,则自动注册为会员
            Member member = memberService.isMember(telephone);
            if (member == null) {
                //说明该用户不是会员,需要自动注册为会员
                member = new Member();
                member.setPhoneNumber(telephone);
                member.setRegTime(new Date());
                memberService.AutoregisterMember(member);
            }
            //第3,向客户端写入cookcie 用来追踪用户的请求
            Cookie cookie = new Cookie("login_member_telephone", telephone);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(cookie);
            //第4  ,将会员信息保存到redis,手机号作为key ,保存时长30分钟,会员对象不能直接保存到redis中,所以需要转换成json
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone, 60 * 30, json);
            return new Result(true, MessageConstant.LOGIN_SUCCESS);

        } else {
            ///验证码输入不正确
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }
}
