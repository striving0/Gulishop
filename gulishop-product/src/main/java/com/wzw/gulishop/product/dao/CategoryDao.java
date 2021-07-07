package com.wzw.gulishop.product.dao;

import com.wzw.gulishop.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author @Kevin
 * @email 2642779862@qq.com
 * @date 2020-12-28 21:12:13
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
