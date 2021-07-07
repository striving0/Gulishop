package com.wzw.gulishop.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.utils.PageUtils;
import com.wzw.gulishop.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 11:08:26
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);
}

