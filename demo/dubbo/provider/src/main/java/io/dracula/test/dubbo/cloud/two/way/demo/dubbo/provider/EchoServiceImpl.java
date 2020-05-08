package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.provider;

import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.EchoService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.SomeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dk
 */
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo")
public class EchoServiceImpl implements EchoService {

    private static Logger logger = LoggerFactory.getLogger(EchoServiceImpl.class);

    @Override
    public SomeMsg echo(SomeMsg someMsg) {
        String originalMsg = someMsg.getMsg();
        SomeMsg newOne = new SomeMsg();
        String newMsg = "echo一下："+originalMsg;
        logger.info("将要返回的："+newMsg);
        newOne.setMsg(newMsg);
        return newOne;
    }

}
