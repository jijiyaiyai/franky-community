package com.franky.community.control.interceptor;

import com.franky.community.entity.User;
import com.franky.community.service.AnalyseService;
import com.franky.community.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AnalyseInterceptor implements HandlerInterceptor {
    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private HostHolder hostHolder;

    //每次有用户登录都会统计新的数据
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV
        String ip = request.getRemoteHost();
        analyseService.recordUV(ip);

        // 统计DAU
        User user = hostHolder.getUser();
        if (user != null) {
            analyseService.recordDAU(user.getId());
        }
        return true;
    }
}
