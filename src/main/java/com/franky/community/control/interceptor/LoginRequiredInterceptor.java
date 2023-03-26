package com.franky.community.control.interceptor;


import com.franky.community.control.UserController;
import com.franky.community.tool.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果在线程池中找不到这个user就说明他根本没登录，重定向去让他登录
        //但要注意不是全部的方法都需要拦截,只需要拦截get方法转到用户设置页的那个方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod)handler;
//            System.out.println(handlerMethod.getMethod());
//            System.out.println(UserController.class.getMethod("getSettingPage"));
            if(handlerMethod.getMethod()
                    .equals(UserController.class.getMethod("getSettingPage"))
                    && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
