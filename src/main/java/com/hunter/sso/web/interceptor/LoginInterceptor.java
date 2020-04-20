package com.hunter.sso.web.interceptor;

import com.hunter.sso.web.domain.SpUser;
import com.hunter.sso.web.utils.CookieUtils;
import com.hunter.sso.web.utils.MapperUtils;
import com.hunter.sso.web.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Hunter
 * @date 2020/4/20
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${sso.hosts}")
    String SSO_HOSTS;

    RedisUtil redisUtil;

    final String CURRENT_USER = "CURRENT_USER";

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String url = request.getServletPath();
        String token = CookieUtils.getCookieValue(request, "token");
        if (StringUtils.isBlank(token)) {
            try {
                response.sendRedirect(String.format("%s/login?url=%s", SSO_HOSTS, "http://localhost:8080" + url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        HttpSession session = request.getSession();
        SpUser spUser = (SpUser) session.getAttribute(CURRENT_USER);
        if (null == spUser) {
            String username = redisUtil.get(token);
            if (StringUtils.isNotBlank(username)) {
                String json = redisUtil.get(username);
                if (StringUtils.isNotBlank(json)) {
                    try {
                        spUser = MapperUtils.json2pojo(json, SpUser.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (null == spUser) {
                try {
                    response.sendRedirect(String.format("%s/login?url=%s", SSO_HOSTS, "http://localhost:8080" + url));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            request.getSession().setAttribute(CURRENT_USER,spUser);
        }
        return true;
    }
}
