package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 检查用户是否已经登录
 */
//日志
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")//检查用户是否登录，过滤器名称  拦截的路径
public class LoginCheckFilter implements Filter {
    //路径匹配器, 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;
         // 1.获取本次请求的URI
         String requestURI = request.getRequestURI();
//         log.info("拦截到请求：{}",requestURI);
            //定义不需要处理的请求路径
         String[] urls = new String[]{
           "/employee/login",
           "/employee/logout",
           "/backend/**",
           "/front/**",
         };
         // 2.判断本次请求是否需要处理
         boolean check = check(urls,requestURI);
         // 3.如果不需要处理则直接放行
        log.info("本次请求{}不需要处理",requestURI);
        if (check == true){
            filterChain.doFilter(request,response);
            return;
        }
         // 4.判断登录状态，如果已登录则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);//设置值

            filterChain.doFilter(request,response);
            return;
        }
         // 5.如果未登录，则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString((R.error("NOTLOGIN"))));
        return;
    }
    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match == true){
                return true;
            }
        }
        return false;
    }
}
