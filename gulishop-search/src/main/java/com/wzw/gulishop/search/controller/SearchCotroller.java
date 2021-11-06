package com.wzw.gulishop.search.controller;

import com.wzw.gulishop.search.service.MallSearchService;
import com.wzw.gulishop.search.vo.SearchParam;
import com.wzw.gulishop.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther Kevin
 * @ClassName SearchCotroller
 * @Date 2021.08.22 16:59
 */


@Controller
public class SearchCotroller {


    @Autowired
    MallSearchService mallSearchService;

    /**
     * 使用SpringMVC将页面提交过来的所有请求查询参数封装成指定的对象
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){

        //根据传递过来的页面参数，去es中检索商品
        SearchResult result =  mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }
}
