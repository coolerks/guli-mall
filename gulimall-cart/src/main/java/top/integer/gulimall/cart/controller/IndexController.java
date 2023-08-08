package top.integer.gulimall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class IndexController {
    @GetMapping("/cartKList.html")
    public String cartList() {
        return "cartList";
    }

    @GetMapping("/success.html")
    public String success() {
        return "success";
    }
}
