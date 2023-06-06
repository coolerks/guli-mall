package top.integer.gulimall.search.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import top.integer.gulimall.search.entry.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
}
