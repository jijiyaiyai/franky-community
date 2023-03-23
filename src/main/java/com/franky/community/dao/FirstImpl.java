package com.franky.community.dao;

import com.franky.community.dao.FirstDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("firstImpl") // 定义bean的别名
@Primary //表示如果有多个实现，这个类会被优先装配
public class FirstImpl implements FirstDao {
    @Override
    public String selectAll() {
        return "This is firstImpl";
    }

}
