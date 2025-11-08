package com.shuttleverse.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.shuttleverse.connect.client")
@EnableJpaRepositories
public class ShuttleverseConnectApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShuttleverseConnectApplication.class, args);
  }

}
