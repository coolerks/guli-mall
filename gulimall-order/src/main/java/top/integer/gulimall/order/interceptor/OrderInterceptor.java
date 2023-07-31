package top.integer.gulimall.order.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.integer.common.vo.MemberEntity;
import top.integer.common.vo.UserInfo;
import top.integer.common.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OrderInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> loginUser = (LinkedHashMap<String, Object>) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
        UserInfo user = new UserInfo();
        user.setUserId(Long.parseLong(loginUser.get("id").toString()));
        user.setTempUser(false);
        OrderInterceptor.loginUser.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        loginUser.remove();
    }
}
