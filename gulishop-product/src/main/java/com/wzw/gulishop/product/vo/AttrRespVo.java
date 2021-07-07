package com.wzw.gulishop.product.vo;

import lombok.Data;

/**
 * @auther Kevin
 * @ClassName AttrRespVo
 * @Date 2021.04.26 20:25
 */

@Data
public class AttrRespVo extends AttrVo {

    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * "groupName": "主体", //所属分组名字
     *
     * */

    /**
     * 所属分类名字
     */
    private String catelogName;

    /**
     * 所属分组名字
     */
    private String groupName;

    private Long[] catelogPath;
}
