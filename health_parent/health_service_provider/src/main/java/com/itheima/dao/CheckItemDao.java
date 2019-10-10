package com.itheima.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    public void addItem(CheckItem checkItem);
    public Page<CheckItem> selectByCondition(String queryString);
    public Long findCountByCheckitemID(Integer id);

    void deleteById(Integer id);

    void edit(CheckItem checkItem);

    List<CheckItem> findAll();
}
