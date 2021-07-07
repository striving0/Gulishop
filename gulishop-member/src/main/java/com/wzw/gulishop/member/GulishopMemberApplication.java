package com.wzw.gulishop.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、想要远程调用别的服务
 * 1）、引入open-feign
 * 2）、编写一个接口，告诉SpringCloud这个接口需要调用远程服务
 *   1、声明接口的每一个方法都是调用哪个远程服务的那个请求
 * 3）、开启远程调用功能
 * 想要远程调用的步骤：
 * 1 引入openfeign
 * 2 编写一个接口，接口告诉springcloud这个接口需要调用远程服务
 * 	2.1 在接口里声明@FeignClient("gulimall-coupon")他是一个远程调用客户端且要调用coupon服务
 * 	2.2 要调用coupon服务的/coupon/coupon/member/list方法
 * 3 开启远程调用功能 @EnableFeignClients，要指定远程调用功能放的基础包
 */
//@EnableFeignClients开启feign远程客户端的调用功能
//@EnableDiscoveryClient用于向consul或者zookeeper作为注册中心的时候提供注册服务（就是提供注册服务的）将一个微服务注册到Eureka Server
@EnableFeignClients(basePackages="com.wzw.gulishop.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulishopMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulishopMemberApplication.class, args);
    }

}
