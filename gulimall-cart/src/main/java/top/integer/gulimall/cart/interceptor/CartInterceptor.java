package top.integer.gulimall.cart.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.integer.gulimall.cart.vo.UserInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfo> userInfo = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> loginUser = (LinkedHashMap<String, Object>) request.getSession().getAttribute("loginUser");
        UserInfo user = new UserInfo();
        user.setTempUser(true);

        if (loginUser != null) {
            user.setUserId(Long.parseLong(loginUser.get("id").toString()));
            user.setTempUser(false);
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            cookies = new Cookie[0];
        }

        String userKey = Arrays.stream(cookies).collect(Collectors.toMap(Cookie::getName, Cookie::getValue))
                .get("user-key");

        if (userKey != null) {
            user.setUserKey(userKey);
        } else if (user.isTempUser()) {
            user.setUserKey(UUID.randomUUID().toString().replace("-", ""));
            Cookie cookie = new Cookie("user-key", user.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(3600 * 24 * 30);
            response.addCookie(cookie);
        }
        userInfo.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userInfo.remove();
    }
}
