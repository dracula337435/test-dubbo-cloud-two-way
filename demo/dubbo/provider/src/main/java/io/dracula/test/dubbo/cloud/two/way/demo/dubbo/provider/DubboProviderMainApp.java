package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dk
 */
@SpringBootApplication
@EnableDubbo
public class DubboProviderMainApp {

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        SpringApplication.run(DubboProviderMainApp.class, args);
    }

}
