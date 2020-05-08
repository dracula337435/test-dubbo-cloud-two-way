package io.dracula.test.dubbo.cloud.two.way.demo.cloud.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.tsf.annotation.EnableTsf;

/**
 * @author dk
 */
@EnableTsf
@SpringBootApplication
public class CloudServerMainApp {

    /**
     *
     * @param args
     */
    public static void main(String[] args){
       SpringApplication.run(CloudServerMainApp.class, args);
    }

}
