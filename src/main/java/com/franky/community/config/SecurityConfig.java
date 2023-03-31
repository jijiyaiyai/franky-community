package com.franky.community.config;

import com.franky.community.tool.CommunityConstant;
import com.franky.community.tool.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权:
        // 在访问以下web路径的时候不区分权限，但是需要登录
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top", "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR // 版主才可以置顶加精
                )
                .antMatchers(
                        "/discuss/delete"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN  // 管理员才可以删帖
                )
                .anyRequest().permitAll() // 除了这些请求之外，其他的都允许游客访问
                .and().csrf().disable();

        // 权限不够时的处理
        // 没有登录
        // 权限不足
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    //如果是异步请求，则返回json
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦!"));
                    } else {
                        //打回到登录页面让用户登录
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                })
                .accessDeniedHandler((request, response, e) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    } else {
                        //跳转到拒绝访问
                        response.sendRedirect(request.getContextPath() + "/denied");
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 这样的话在logout之后的拦截器不会触发
        // 覆盖它默认的逻辑，执行自己的退出逻辑
        http.logout().logoutUrl("/securitylogout");
    }
}
