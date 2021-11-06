package com.wzw.gulishop.search.service;

import com.wzw.gulishop.search.vo.SearchParam;
import com.wzw.gulishop.search.vo.SearchResult;

/**
 * @auther Kevin
 * @ClassName MallSearchService
 * @Date 2021.08.22 17:35
 */
public interface MallSearchService {


    /**
     *
     * @param param 检索的所有参数
     * @return 包含页面的所有信息
     */
    SearchResult search(SearchParam param);
}
