package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealService {
    public void add(Setmeal setmeal, Integer[]checkgroupIds);

    PageResult findPage(QueryPageBean queryPageBean);

    int delete(int id);

    List<Setmeal> getAllSetmeal();

    Setmeal findById(int id);
    List<Map<String,Object>> findSetmealCount ();
}
