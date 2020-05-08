package io.dracula.test.dubbo.cloud.two.way.demo.cloud.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.tsf.annotation.EnableTsf;
import org.springframework.web.client.RestTemplate;

/**
 * @author dk
 */
@SpringBootApplication
@EnableTsf
@EnableFeignClients
public class CloudClientMainApp {

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        SpringApplication.run(CloudClientMainApp.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
