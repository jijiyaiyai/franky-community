package com.franky.community.control.interceptor;


import com.franky.community.control.LikeController;
import com.franky.community.control.UserController;
import com.franky.community.tool.HostHolder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果在线程池中找不到这个user就说明他根本没登录，重定向去让他登录
        //但要注意不是全部的方法都需要拦截
        if(handler instanceof HandlerMethod){
            Method handlerMethod = ((HandlerMethod) handler).getMethod();
            Set<Method> MethodSet = new HashSet<>();
            MethodSet.add(UserController.class.getMethod("getSettingPage"));
            //MethodSet.add(LikeController.class.getMethod("like", int.class, int.class, int.class));

            if(MethodSet.contains(handlerMethod)
                    && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
