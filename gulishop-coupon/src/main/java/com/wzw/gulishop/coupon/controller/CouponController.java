package com.wzw.gulishop.coupon.controller;

import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.gulishop.coupon.entity.CouponEntity;
import com.wzw.gulishop.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 优惠券信息
 *
 * @author Kevin
 * @email 2642779862@qq.com
 * @date 2021-01-18 11:08:26
 */

/**
 * 1、如何使用Nacos作为配置中心统一管理配置
 *
 * 1）、引入依赖，
 *         <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 * 2）、创建一个bootstrap.properties。
 *      spring.application.name=gulimall-coupon
 *      spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 * 3）、需要给配置中心默认添加一个叫 数据集（Data Id）gulimall-coupon.properties。默认规则，应用名.properties
 * 4）、给 应用名.properties 添加任何配置
 * 5）、动态获取配置。
 *      @RefreshScope：动态获取并刷新配置
 *      @Value("${配置项的名}")：获取到配置。
 *      如果配置中心和当前应用的配置文件中都配置了相同的项，优先使用配置中心的配置。
 *
 * 2、细节
 *  1）、命名空间：配置隔离；
 *      默认：public(保留空间)；默认新增的所有配置都在public空间。
 *      1、开发，测试，生产：利用命名空间来做环境隔离。
 *         注意：在bootstrap.properties；配置上，需要使用哪个命名空间下的配置，
 *         spring.cloud.nacos.config.namespace=9de62e44-cd2a-4a82-bf5c-95878bd5e871
 *      2、每一个微服务之间互相隔离配置，每一个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置
 *
 *  2）、配置集：所有的配置的集合
 *
 *  3）、配置集ID：类似文件名。
 *      Data ID：类似文件名
 *
 *  4）、配置分组：
 *      默认所有的配置集都属于：DEFAULT_GROUP；
 *      1111，618，1212
 *
 * 项目中的使用：每个微服务创建自己的命名空间，使用配置分组区分环境，dev，test，prod
 *
 * 3、同时加载多个配置集
 * 1)、微服务任何配置信息，任何配置文件都可以放在配置中心中
 * 2）、只需要在bootstrap.properties说明加载配置中心中哪些配置文件即可
 * 3）、@Value，@ConfigurationProperties。。。
 * 以前SpringBoot任何方法从配置文件中获取值，都能使用。
 * 配置中心有的优先使用配置中心中的，
 *
 *
 */
@RefreshScope
@RestController
@RequestMapping("coupon/coupon" )
public class CouponController {



    @Value("${coupon.user.name}")
    private String name;
    @Value("${coupon.user.age}")
    private Integer age;


    @RequestMapping("/test")
    public R test(){
        return R.ok().put("name", name).put("age", age);
    }


    @Autowired
    private CouponService couponService;


    @RequestMapping("/member/list")
    public R membercoupons(){

        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("满100减15");
        return R.ok().put("coupons", Arrays.asList(couponEntity));
    }



    /**
     * 列表
     */
    @RequestMapping("/list" )
//@RequiresPermissions("coupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}" )
    //@RequiresPermissions("coupon:coupon:info" )
    public R info(@PathVariable("id" ) Long id) {
            CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save" )
    //@RequiresPermissions("coupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon) {
            couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update" )
    //@RequiresPermissions("coupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon) {
            couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete" )
    //@RequiresPermissions("coupon:coupon:delete")
    public R delete(@RequestBody Long[] ids) {
            couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
