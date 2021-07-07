package com.wzw.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Kevin
 * @ClassName SkuReductionTo
 * @Date 2021.05.22 21:06
 */

@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
