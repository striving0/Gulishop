package com.wzw.gulishop.ware.vo;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class PurchaseDoneVo {
    @NonNull
    private Long id;

    private List<com.wzw.gulishop.ware.vo.PurchaseItemDoneVo> items;

    public PurchaseDoneVo(){}
}
