package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dk
 */
@SpringBootApplication
@EnableDubbo
public class DubboConsumerMainApp {

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        SpringApplication.run(DubboConsumerMainApp.class, args);
    }

}
