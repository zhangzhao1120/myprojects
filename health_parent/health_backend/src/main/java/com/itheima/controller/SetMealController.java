package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetMealController {




    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetMealService setMealService;

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        return  setMealService.findPage(queryPageBean);
    }


    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[]checkgroupIds){
        try{
            setMealService.add(setmeal,checkgroupIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
      return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){

        String originalFilename = imgFile.getOriginalFilename();//获得原始的文件全名
        int i = originalFilename.lastIndexOf(".");//用"."进行分割获得最后一个"."的索引位置
        String houzhui = originalFilename.substring(i - 1);//获取原始文件名的后缀名字包括.  如 .JPG
        String fileName= UUID.randomUUID().toString()+houzhui;
        System.out.println(imgFile);
        try {
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //将文件上传到七牛云后将文件名保存到一个大集合中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

    @RequestMapping("/delete")
    public Result delete(int id){
        try{
            int index = setMealService.delete(id);
            if(-1==index){
                return new Result(false,"不能删除已关联检查组的套餐哦");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除套餐失败");
        }
        return new Result(true,"删除套餐成功");
    }
}
