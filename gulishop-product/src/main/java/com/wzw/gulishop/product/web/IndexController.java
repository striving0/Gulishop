package com.wzw.gulishop.product.web;

import com.wzw.gulishop.product.entity.CategoryEntity;
import com.wzw.gulishop.product.service.CategoryService;
import com.wzw.gulishop.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @auther Kevin
 * @ClassName IndexController
 * @Date 2021.06.30 0:35
 */

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;


    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){


        // 1、TODO 1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        //视图解析器进行拼接
        // classpath:/templates/ +返回+ .html
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }



    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

}
