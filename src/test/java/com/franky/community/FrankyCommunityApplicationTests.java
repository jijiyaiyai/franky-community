package com.franky.community;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FrankyCommunityApplication.class)
class FrankyCommunityApplicationTests implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

//    private ApplicationContext applicationContext;
//
//    @Override   //传入一个spring容器并注入当前这个类
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//
//    @Test
//    public void testApplicationContext() {
//        System.out.println(this.applicationContext);
//        FirstDao firstDao = applicationContext.getBean(FirstDao.class);
//        System.out.println(firstDao);
//        System.out.println(firstDao.selectAll());
//        firstDao = applicationContext.getBean("firstImpl",FirstDao.class);
//        System.out.println(firstDao.selectAll());
//    }
//
//    @Test
//    public void testBeanManagement(){
//        FirstService firstService = applicationContext.getBean(FirstService.class);
//        System.out.println(firstService);
//    }
//
//    //这个自动装配就可以取代上面的appcontext，避免了自己取bean的麻烦
//    @Autowired
//    @Qualifier("firstImpl") //可以指定想要注入哪个实现类
//    private FirstDao firstDao;
//
//    @Test
//    public void testDI(){
//        System.out.println(firstDao);
//        System.out.println(firstDao.selectAll());
//    }



}
