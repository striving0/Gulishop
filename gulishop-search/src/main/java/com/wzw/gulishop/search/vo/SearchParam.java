package com.wzw.gulishop.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @auther Kevin
 * @ClassName SearchParam
 * @Date 2021.08.22 17:32
 */

/**
 * 封装页面所有传递过来的查询条件
 * catalog3Id=225&keyword=小米sort=saleCount_asc
 */


@Data
public class SearchParam {

    private String keyword;//页面传递过来的
    private Long catalog3Id;//三级分类ID

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hostScore_asc/desc
     */
    private String sort;//排序条件

    /**
     * 过滤条件
     * hasStock(是否有货),skuPrice(区间),brandID(品牌ID),catalog3Id,attrs
     * hasStock 0/1
     * skuPrice 1_500/_500/500_
     * attrs2_5寸:6寸
     * */
    private Integer hstock;//是否只显示有货 0(无库存) 1(有库存)
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌Id,可以多选
    private List<String> attrs;//按照属性
    private Integer pageNum = 1;//页码

}
