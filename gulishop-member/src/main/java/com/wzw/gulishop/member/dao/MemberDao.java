package com.wzw.gulishop.member.dao;

import com.wzw.gulishop.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 11:57:33
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
