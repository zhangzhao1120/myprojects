package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkitem")
public class CheckItemController {
    @Reference
    public CheckItemService checkItemService;

    //添加检查组时查询所有的检查项
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
           List<CheckItem> list= checkItemService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        }catch (Exception e){
            e.printStackTrace();
            //说明添加失败
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }
    //删除检查项
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    @RequestMapping("/delete")

    public Result deleteById(Integer id){
        try {
            int result=checkItemService.deleteById(id);
            if(-1 == result){
                return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL_CONTAIN);
            }
        }catch (Exception e){
            e.printStackTrace();
            //说明删除失败
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        //说明删除检查项成功
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);

    }

    //分页查询的方法
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        PageResult pageResult = checkItemService.queryPage(queryPageBean);

        return pageResult;

    }


//添加检查项
    @RequestMapping("/add")
    public Result addItem(@RequestBody CheckItem checkItem){

        try {
            checkItemService.addItem(checkItem);
        }catch (Exception e){
            e.printStackTrace();
            //说明添加失败
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }
        //说明添加检查项成功
        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);

    }
    //修改检查项
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem){

        try {
            checkItemService.edit(checkItem);
        }catch (Exception e){
            e.printStackTrace();
            //说明添加失败
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
        //说明添加检查项成功
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);

    }


}
