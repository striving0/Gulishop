package com.wzw.gulishop.ware.dao;

import com.wzw.gulishop.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * εεεΊε­
 *
 * @author AdverseQ
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 07:21:00
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    Long getSkuStock(Long skuId);
}
