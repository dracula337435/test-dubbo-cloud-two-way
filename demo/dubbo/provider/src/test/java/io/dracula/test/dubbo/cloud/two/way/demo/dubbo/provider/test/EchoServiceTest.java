package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider.test;

import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider.EchoService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider.SomeMsg;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author dk
 */
@RunWith(SpringRunner.class)
public class EchoServiceTest {

    private static Logger logger = LoggerFactory.getLogger(EchoServiceTest.class);

    @Reference
    private EchoService echoService;

    @Test
    public void test(){
        SomeMsg someMsg = new SomeMsg();
        someMsg.setMsg("gxk");
        logger.info(echoService.echo(someMsg).getMsg());
    }

    /**
     * @author dk
     */
    @Configuration
    @DubboComponentScan
    public static class Config{

        @Bean
        public ApplicationConfig applicationConfig(){
            return new ApplicationConfig("test-provider");
        }

        @Bean
        public RegistryConfig registryConfig(){
            return new RegistryConfig("tsfconsul://localhost:8500");
        }

    }

}
