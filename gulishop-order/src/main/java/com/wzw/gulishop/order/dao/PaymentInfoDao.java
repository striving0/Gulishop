package com.wzw.gulishop.order.dao;

import com.wzw.gulishop.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 12:25:20
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
