package com.minh.realtime_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.minh.realtime_gateway.*", "com.minh.common"})
@EnableMethodSecurity
public class RealtimeGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeGatewayApplication.class, args);
	}

}
