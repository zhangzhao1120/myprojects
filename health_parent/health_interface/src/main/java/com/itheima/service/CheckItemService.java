package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {
    //添加检查项
    public void addItem(CheckItem checkItem);
    //分页查询的方法
    public PageResult queryPage(QueryPageBean queryPageBean);

    int deleteById(Integer id);

    void edit(CheckItem checkItem);

    List<CheckItem> findAll();
}
