package top.integer.gulimall.search.service;

import top.integer.gulimall.search.vo.SearchParam;
import top.integer.gulimall.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
