package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api;

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
