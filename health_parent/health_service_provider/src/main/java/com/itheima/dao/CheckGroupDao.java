package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
//<!--在检查组里新增基本信息-->
    public void addGroup(CheckGroup checkGroup);

    public void addGroupItem(Map map);


    Page<CheckGroup> findPage(String queryString);

    List<Integer> selectCheckIdsByGroupId(Integer id);

    void editbase(CheckGroup checkGroup);

    void deleteRelByGroupId(Integer groupId);

    List<CheckGroup> findAll();

    Long finditemsById(int id);

    Long findmealsById(int id);

    void delete(int id);
}
