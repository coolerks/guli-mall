package top.integer.gulimall.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.integer.common.utils.R;
import top.integer.gulimall.cart.interceptor.CartInterceptor;
import top.integer.gulimall.cart.service.CartService;
import top.integer.gulimall.cart.vo.CartItemVo;
import top.integer.gulimall.cart.vo.CartVo;
import top.integer.gulimall.cart.vo.UserInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RequestMapping("/")
@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @GetMapping("/cart.html")
    public String cartList(Model model) {
        UserInfo userInfo = CartInterceptor.userInfo.get();
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        System.out.println("cartVo = " + cartVo);
        return "cartList";
    }

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItemVo> getCurrentUserCartItems() {
        UserInfo userInfo = CartInterceptor.userInfo.get();
        System.out.println("userInfo = " + userInfo);
        return cartService.currentUserCartItems(userInfo.getUserId());
    }

    @GetMapping("/addCartItem")
    public String success(Long skuId, Integer num, Model model) throws ExecutionException, InterruptedException {
        CartItemVo cartItemVo = cartService.addToCart(skuId, num);
        return "redirect:http://cart.gulimall.com/success?skuId=" + skuId;
    }


    @GetMapping("/success")
    public String success(Long skuId, Model model) {
        CartItemVo cartItemVo = cartService.getSkuInfo(skuId);
        model.addAttribute("item", cartItemVo);
        return "success";
    }

    @PostMapping("/checkCart")
    @ResponseBody
    public R checkOrUncheck(Long skuId, Boolean isChecked) {
        cartService.checkOrUncheck(skuId, isChecked);
        return R.ok();
    }

    @PostMapping("/countItem")
    @ResponseBody
    public R countItem(Long skuId, Long count) {
        cartService.countItem(skuId, count);
        return R.ok();
    }

    @PostMapping("/deleteItem")
    @ResponseBody
    public R deleteItem(Long skuId) {
        System.out.println("skuId = " + skuId);
        cartService.deleteItem(skuId);
        return R.ok();
    }
}
