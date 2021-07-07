package com.wzw.gulishop.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulishopWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulishopWareApplication.class, args);
	}

}
