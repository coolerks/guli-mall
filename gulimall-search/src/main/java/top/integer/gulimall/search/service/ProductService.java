package top.integer.gulimall.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.integer.gulimall.search.entry.Product;
import top.integer.gulimall.search.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    public void save(List<Product> list) {
        repository.saveAll(list);
    }
}
