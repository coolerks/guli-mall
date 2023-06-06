package top.integer.gulimall.search.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import top.integer.gulimall.search.entry.Blog;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {
    Blog getBlogById(Long id);
}
