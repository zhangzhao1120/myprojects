package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass =CheckGroupService.class )
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {

//        在检查组里新增基本信息
        checkGroupDao.addGroup(checkGroup);
   //获取刚插入数据库的检查组的id值
        Integer groupId = checkGroup.getId();
        System.out.println(groupId);
//        在中间表中保存对应的关联信息
        if(checkitemIds!=null && checkitemIds.length>0 ){
            for (Integer checkitemId : checkitemIds) {
                Map<String,Integer>map=new HashMap<>();
                map.put("groupId",groupId);
                map.put("checkitemId",checkitemId);
                System.out.println(map);
                checkGroupDao.addGroupItem(map);
            }

        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page <CheckGroup> page=checkGroupDao.findPage(queryString);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Integer> selectCheckIdsByGroupId(Integer id) {
        return checkGroupDao.selectCheckIdsByGroupId(id);
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //对检查组的基本信息进行修改提交
        checkGroupDao.editbase(checkGroup);
        //把中间表中检查组已关联的检查项关系清空,
        Integer groupId = checkGroup.getId();
        checkGroupDao.deleteRelByGroupId(groupId);
        // 根据页面传过来的checkitemIds重新进行关联
        this.addGroupItem(checkGroup,checkitemIds);
    }

    @Override
    public  List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    @Override
    public int delete(int id) {
        Long checkItemsIds= checkGroupDao.finditemsById(id);
        Long checkmealsIds= checkGroupDao.findmealsById(id);

        if(checkItemsIds==0 && checkmealsIds==0){
            checkGroupDao.delete(id);
        }else{
            return -1;
        }
        return 1;
    }

    public void addGroupItem(CheckGroup checkGroup, Integer[] checkitemIds){
        Integer groupId = checkGroup.getId();
        System.out.println(groupId);
//        在中间表中保存对应的关联信息
        if(checkitemIds!=null && checkitemIds.length>0 ){
            for (Integer checkitemId : checkitemIds) {
                Map<String,Integer>map=new HashMap<>();
                map.put("groupId",groupId);
                map.put("checkitemId",checkitemId);
                System.out.println(map);
                checkGroupDao.addGroupItem(map);
            }

        }
    }

}
