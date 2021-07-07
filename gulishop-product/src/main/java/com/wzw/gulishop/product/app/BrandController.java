package com.wzw.gulishop.product.app;

import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.common.valid.AddGroup;
import com.wzw.common.valid.UpdateStatusGroup;
import com.wzw.gulishop.product.entity.BrandEntity;
import com.wzw.gulishop.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-06 20:13:16
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand /*, BindingResult result*/) {
//        if (result.hasErrors()) {
//
//            //1.获取错误的校验结果
//            Map<String, String> map = new HashMap<>();
//            result.getFieldErrors().forEach((item) -> {
//                //FieldError获取到错误信息
//                String message = item.getDefaultMessage();
//                //获取错误的属性的名字
//                String field = item.getField();
//                map.put(field, message);
//
//            });
//
//            return R.error(400, "提交的数据不合法").put("data", map);
//
//        } else {
//            brandService.save(brand);
//        }

        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(com.wzw.common.valid.UpdateGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateDeatil(brand);
        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
