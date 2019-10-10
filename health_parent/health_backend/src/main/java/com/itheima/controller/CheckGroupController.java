package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

        //注入zookeeper中的服务
    @Reference
    private CheckGroupService checkGroupService;
//  编辑检查组的信息
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup,Integer[]checkitemIds){
        System.out.println(checkitemIds);
        try{
            checkGroupService.edit(checkGroup,checkitemIds);
            return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }

    //根据检查组id查询对应的所有检查项id
    @RequestMapping("/selectCheckIdsByGroupId")
    public Result selectCheckIdsByGroupId(Integer id){

        try{
            List<Integer> list=checkGroupService.selectCheckIdsByGroupId(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        return  checkGroupService.findPage(queryPageBean);

    }


    //新增检查组
    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[]checkitemIds){
        System.out.println(checkitemIds);
       try{
           checkGroupService.add(checkGroup,checkitemIds);
           return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
       }catch(Exception e){
           e.printStackTrace();
            return new Result(false,MessageConstant.ADD_CHECKGROUP_FAIL);
        }
    }
    //新增检查组
    @RequestMapping("/findAll")
    public Result findAll(){
        try{
            List<CheckGroup>list=checkGroupService.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
    //删除检查组
    @RequestMapping("/delete")
    public Result delete(int id){
        try{
            int index = checkGroupService.delete(id);
            if(-1==index){
                return new Result(false,"不能删除有关联关系的检查组哦");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除检查组失败");
        }
        return new Result(true,"嘿,真棒,删除检查组成功!!!");
    }
}
