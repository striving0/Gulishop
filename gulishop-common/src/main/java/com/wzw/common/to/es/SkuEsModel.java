package com.wzw.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Kevin
 * @ClassName SkuEsModel
 * @Date 2021.06.20 21:31
 */

@Data
public class SkuEsModel {
    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attrs> attrs;

//    public void setAttrs(List<Attrs> attrsList) {
//        this.attrs = attrs;
//    }

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
