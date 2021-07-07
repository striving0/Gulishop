package com.wzw.gulishop.product.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @auther Kevin
 * @ClassName WareFeignService
 * @Date 2021.06.23 1:13
 */

@FeignClient("gulishop-ware")
public interface WareFeignService {


    /**
     * 查询是否有库存
     */
    @PostMapping("/ware/waresku/hasstock")
    R  getSkuHasStock(@RequestBody List<Long> skuIds);
}
