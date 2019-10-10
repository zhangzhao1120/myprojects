package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealDao {
    public void add(Setmeal setmeal);
    public void addRel(Map map);

    Page<Setmeal> findPage(String queryString);

    void delete(int id);

    Long findById(int id);

    List<Setmeal> getAllSetmeal();

    Setmeal findBySetmealId(int id);

    List<Map<String, Object>> findSetmealCount();

}
