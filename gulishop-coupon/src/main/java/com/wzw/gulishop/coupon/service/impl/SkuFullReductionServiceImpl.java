package com.wzw.gulishop.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.to.MemberPrice;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;
import com.wzw.gulishop.coupon.dao.SkuFullReductionDao;
import com.wzw.gulishop.coupon.entity.MemberPriceEntity;
import com.wzw.gulishop.coupon.entity.SkuFullReductionEntity;
import com.wzw.gulishop.coupon.entity.SkuLadderEntity;
import com.wzw.gulishop.coupon.service.MemberPriceService;
import com.wzw.gulishop.coupon.service.SkuFullReductionService;
import com.wzw.gulishop.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    SkuFullReductionService skuFullReductionService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {
        //1保存满减打折sku的优惠满减信息;gulimall_sms->sms_sku_ladder
        final SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(reductionTo.getSkuId());
        skuLadderEntity.setFullCount(reductionTo.getFullCount());
        skuLadderEntity.setDiscount(reductionTo.getDiscount());
        skuLadderEntity.setAddOther(reductionTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);

        //2.sms_sku_full_reduction
        final SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(reductionTo, reductionEntity);
        if(reductionEntity.getFullPrice().compareTo(new BigDecimal(0))==1){
            this.save(reductionEntity);
        }


        //3.\sms_member_price
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        final List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
            final MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(reductionTo.getSkuId());
            priceEntity.setMemberLevelId(item.getId());
            priceEntity.setMemberLevelName(item.getName());
            priceEntity.setMemberPrice(item.getPrice());
            priceEntity.setAddOther(1);

            return priceEntity;

        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal(0))==1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);


    }

}