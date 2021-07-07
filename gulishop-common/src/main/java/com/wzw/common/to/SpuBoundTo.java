package com.wzw.gulishop.coupon.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther Kevin
 * @ClassName SpuBoundTo
 * @Date 2021.05.22 17:02
 */


@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;


}
