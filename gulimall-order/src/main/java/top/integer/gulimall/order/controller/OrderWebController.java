package top.integer.gulimall.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.integer.gulimall.order.service.OrderService;
import top.integer.gulimall.order.vo.OrderConfirmVo;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/")
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/confirm.html")
    public String page() {
        return "confirm";
    }

    @GetMapping("/detail.html")
    public String page2() {
        return "detail";
    }

    @GetMapping("/list.html")
    public String page3() {
        return "list";
    }

    @GetMapping("/pay.html")
    public String page4() {
        return "pay";
    }

    /**
     * 订单确认
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirm", orderConfirmVo);
        System.out.println("orderConfirmVo = " + orderConfirmVo);
        return "confirm";
    }


}
