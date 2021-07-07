package com.wzw.gulishop.product.feign;

import com.wzw.common.to.es.SkuEsModel;
import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @auther Kevin
 * @ClassName SearchFeignService
 * @Date 2021.06.23 23:08
 */


@FeignClient("gulishop-search")
public interface SearchFeignService {

    //商品上架
    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
