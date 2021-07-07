package com.wzw.gulishop.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.gulishop.product.entity.SpuInfoEntity;
import com.wzw.gulishop.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author @Kevin
 * @email 2642779862@qq.com
 * @date 2020-12-28 21:12:13
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);


    void up(Long spuId);
}

