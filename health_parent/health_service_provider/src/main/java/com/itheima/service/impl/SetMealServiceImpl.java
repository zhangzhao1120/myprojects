package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetMealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetMealService.class)
@Transactional
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealDao setMealDao;
    @Autowired
    private JedisPool jedisPool;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setMealDao.add(setmeal);
        String fileName = setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);
        Integer id = setmeal.getId();
        this.setRel(id,checkgroupIds);
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal>page=setMealDao.findPage(queryString);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public int delete(int id) {
       Long checkGroupIds= setMealDao.findById(id);

       if(checkGroupIds==0){
           setMealDao.delete(id);
       }else{
           return -1;
       }
       return 1;
    }

    @Override
    public List<Setmeal> getAllSetmeal() {
        return setMealDao.getAllSetmeal();
    }
//根据套餐id查询套餐的具体内容
    @Override
    public Setmeal findById(int id) {
        return setMealDao.findBySetmealId(id);
    }
    //获取套餐的饼形图
    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setMealDao.findSetmealCount();
    }

    public void setRel( Integer id,Integer[] checkgroupIds){
        if(checkgroupIds!=null && checkgroupIds.length>0){
            Map<String,Integer> map=new HashMap<>();
            for (Integer checkgroupId : checkgroupIds) {
                map.put("id",id);
                map.put("checkgroupId",checkgroupId);
                setMealDao.addRel(map);
            }
        }
    }
}
