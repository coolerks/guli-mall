package top.integer.gulimall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.integer.common.utils.R;
import top.integer.gulimall.search.entry.Product;
import top.integer.gulimall.search.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/search")
public class ProductController {
    @Autowired
    private ProductService service;
    @PostMapping("/save/product")
    public R save(@RequestBody List<Product> list) {
        service.save(list);
        return R.ok();
    }

}
