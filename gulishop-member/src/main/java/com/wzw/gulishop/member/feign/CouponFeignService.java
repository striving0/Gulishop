package com.wzw.gulishop.member.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther Kevin
 * @ClassName CouponFeignService
 * @Date 2021.01.18 20:41
 */
/*
 * 想要远程调用的步骤：
 * 1 引入openfeign
 * 2 编写一个接口，接口告诉springcloud这个接口需要调用远程服务
 * 	2.1 在接口里声明@FeignClient("gulimall-coupon")他是一个远程调用客户端且要调用coupon服务
 * 	2.2 要调用coupon服务的/coupon/coupon/member/list方法
 * 3 开启远程调用功能 @EnableFeignClients，要指定远程调用功能放的基础包
 * */
//@FeignClient声明试的远程调用告诉spring cloud这个接口是一个远程客户端，要调用coupon服务，再去调用coupon服务/coupon/coupon/member/list对应的方法
//gulishop-coupon是在注册中的注册的服务名
@FeignClient("gulishop-coupon")
public interface CouponFeignService {


    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();


}
