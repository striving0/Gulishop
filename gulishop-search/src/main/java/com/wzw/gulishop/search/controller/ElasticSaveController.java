package com.wzw.gulishop.search.controller;

import com.wzw.common.exception.BizCodeEmume;
import com.wzw.common.to.es.SkuEsModel;
import com.wzw.common.utils.R;
import com.wzw.gulishop.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @auther Kevin
 * @ClassName ElasticSeaveController
 * @Date 2021.06.23 21:54
 */

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;


    //商品上架
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){

        Boolean b =false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误：{}",e);
            return R.error(BizCodeEmume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEmume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!b) {return R.ok();}
        else {return R.error(BizCodeEmume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEmume.PRODUCT_UP_EXCEPTION.getMsg());}
    }

}
