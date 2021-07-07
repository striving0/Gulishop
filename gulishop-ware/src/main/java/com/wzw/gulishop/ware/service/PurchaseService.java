package com.wzw.gulishop.ware.service;

import com.wzw.gulishop.ware.vo.MergeVo;
import com.wzw.gulishop.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.gulishop.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author AdverseQ
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 07:21:00
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);

    PageUtils queryUnreceivePage(Map<String, Object> params);
}

