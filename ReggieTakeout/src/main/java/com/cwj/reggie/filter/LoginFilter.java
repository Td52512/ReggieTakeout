package com.cwj.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cwj.reggie.common.BaseContext;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.Employee;
import com.cwj.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class LoginFilter implements Filter {

    //路径比较器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();


        //定义不需要处理的请求
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/",
                "/user/login",
                "/user/sendMsg",
        };

        //判断此次请求是否需要放行
        if (check(uris, uri)) {
            filterChain.doFilter(request, response);
            return;
        }


        Object employee = request.getSession().getAttribute("employee");
        Object user = request.getSession().getAttribute("user");
        log.info("该请求需要处理：{}", uri);
        //处理后台登陆
        if (employee != null) {
            Employee emp = (Employee) employee;
            Long id = emp.getId();
            BaseContext.set(id);
            filterChain.doFilter(request, response);
            return;
        }

        //处理移动端登陆
        if (user != null) {
            User u = (User) user;
            Long userId = u.getId();
            BaseContext.set(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //此请求需要处理且未登录
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    //返回true表示不需要处理的请求
    public boolean check(String[] uris, String uri) {
        for (String u : uris) {
            boolean match = PATH_MATCHER.match(u, uri);
            if (match) {
                return true;
            }
        }

        return false;
    }
}
