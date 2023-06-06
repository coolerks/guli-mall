package top.integer.gulimall.product.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.integer.gulimall.product.entity.CategoryEntity;
import top.integer.gulimall.product.service.CategoryService;
import top.integer.gulimall.product.vo.CataLog1Vo;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping({"/", "/index.html"})
    public String index(Model model) {
        List<CategoryEntity> categories = categoryService.getLevel1Categories();
        model.addAttribute("categories", categories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<CataLog1Vo>> getCatelogJson() {
        return categoryService.getCatelogJson();
    }
}
