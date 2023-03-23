package com.franky.community.service;

import com.franky.community.dao.FirstDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

//@Scope("prototype") //可以取消单例模式
@Service
public class FirstService {

    public FirstService(){
        System.out.println("实例化FirstService");
    }

    @PostConstruct //让spring容器在构造之后自动调用这个方法，post就是后
    public void init(){
        System.out.println("初始化FirstService");
    }

    @PreDestroy //让spring容器在销毁之前自动调用这个方法，pre就是前
    public void destroy(){
        System.out.println("准备销毁FirstService");
    }

    @Autowired
    private FirstDao firstDao;

    //业务层的接口最好跟dao层的不一样
    //业务层依赖dao
    public String findAll(){
        return firstDao.selectAll();
    }
}
