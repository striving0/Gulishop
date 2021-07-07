package com.wzw.gulishop.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class GulishopThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulishopThirdPartyApplication.class, args);
    }

}
