package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

//添加检查项
    @Override
    public void addItem(CheckItem checkItem) {
        checkItemDao.addItem(checkItem);
    }

    //分页查询
    @Override
    public PageResult queryPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> items = checkItemDao.selectByCondition(queryString);
        long total = items.getTotal();
        List<CheckItem> rows = items.getResult();

        return new PageResult(total,rows);
    }

    //根据id删除检查项
    @Override
    public int deleteById(Integer id) {
        //需要先判断此检查项是否被检查组所关联
        Long groupId = checkItemDao.findCountByCheckitemID(id);
        if(groupId>0){
            //说明此检查项被某个检查组关联
           return -1;
        }
            //说明此检查项没有被检查组关联,调用删除方法
       checkItemDao.deleteById(id);

        return 1;
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public List<CheckItem> findAll() {

        return checkItemDao.findAll();
    }
}
