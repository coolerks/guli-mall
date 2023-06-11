package top.integer.gulimall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import top.integer.gulimall.search.service.MallSearchService;
import top.integer.gulimall.search.vo.SearchParam;
import top.integer.gulimall.search.vo.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    private MallSearchService service;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {
        searchParam.setQueryString(request.getQueryString());
        SearchResult searchResult = service.search(searchParam);
        model.addAttribute("result", searchResult);
        return "search";
    }
}
