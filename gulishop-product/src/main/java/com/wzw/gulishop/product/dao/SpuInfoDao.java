package com.wzw.gulishop.product.dao;

import com.wzw.gulishop.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author @Kevin
 * @email 2642779862@qq.com
 * @date 2020-12-28 21:12:13
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") String spuId, @Param("code") int code);
}
