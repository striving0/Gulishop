package com.wzw.gulishop.search.vo;

import com.wzw.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @auther Kevin
 * @ClassName SearchResult
 * @Date 2021.08.22 18:08
 */

@Data
public class SearchResult {

    //查到的所有商品信息
    private List<SkuEsModel> products;

    /**
     * 分页信息
     */
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码


    private List<BrandVo> brands;//当前查询到的结果，所有涉及到的所有品牌

    private List<AttrVo> attrs;//当前查询到的结果，所有涉及到的所有属性
    private List<CatalogVo> catalogs;//当前查询到的结果，所有涉及到的所有分类


//==========================================以上是返回给页面的所有信息===============================

    @Data
    public static class BrandVo{
        private Long branId;//品牌Id
        private String brandName;//品牌名称

        private String brandImg;//品牌图片
    }

    @Data
    public static class AttrVo{
        private Long attrId;//属性Id
        private String attrName;//属性名称

        private List<String> attrValue;//属性值
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;//分类Id
        private String catalogName;//分类名称

    }

}
