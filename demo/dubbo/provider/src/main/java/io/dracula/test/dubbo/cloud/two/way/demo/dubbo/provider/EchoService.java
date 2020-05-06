package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider;

/**
 * @author dk
 */
public interface EchoService {

    /**
     *
     * @param someMsg
     * @return
     */
    SomeMsg echo(SomeMsg someMsg);

}
