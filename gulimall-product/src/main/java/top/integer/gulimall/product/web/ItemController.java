package top.integer.gulimall.product.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.integer.gulimall.product.service.SkuInfoService;
import top.integer.gulimall.product.vo.SkuItemVo;

@Controller
public class ItemController {
    @Autowired
    private SkuInfoService service;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model) {
        SkuItemVo spuItemVo = service.item(skuId);
        System.out.println("spuItemVo = " + spuItemVo);
        model.addAttribute("item", spuItemVo);
        return "item2";
    }
}
