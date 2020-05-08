package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider;

import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.EchoRestService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.EchoService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.SomeMsg;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dk
 */
@org.apache.dubbo.config.annotation.Service(protocol = "rest")
public class EchoRestServiceImpl implements EchoRestService {

    @Autowired
    private EchoService echoService;

    @Override
    public SomeMsg echo(SomeMsg someMsg) {
        return echoService.echo(someMsg);
    }

}
