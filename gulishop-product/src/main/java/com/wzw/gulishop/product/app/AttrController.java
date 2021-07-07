package com.wzw.gulishop.product.app;

import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.gulishop.product.entity.ProductAttrValueEntity;
import com.wzw.gulishop.product.service.AttrService;
import com.wzw.gulishop.product.service.ProductAttrValueService;
import com.wzw.gulishop.product.vo.AttrRespVo;
import com.wzw.gulishop.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 商品属性
 *
 * @author @Kevin
 * @email 2642779862@qq.com
 * @date 2020-12-28 21:12:13
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * /product/attr/base/listforspu/{spuId}
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrlistforspu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrlistforspu(spuId);

        return R.ok().put("data", entities);
    }


    //    /product/attr/base/list/{catelogId}
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type) {


        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);


        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    /**
     * /product/attr/info/{attrId}
     * 查询属性详情
     */
    @RequestMapping("/info/{attrId}")
//    @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//		AttrEntity attr = attrService.getById(attrId);

        AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }


    /**
     * /product/attr/update/{spuId}
     * attrId": 7,
     * "attrName": "入网型号",
     * "attrValue": "LIO-AL00",
     * "quickShow": 1
     */
    @PostMapping("/update/{spuId}")
//    @RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateSpuAttr(spuId, entities);

        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
