package com.wzw.gulishop.order.dao;

import com.wzw.gulishop.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 12:25:20
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
