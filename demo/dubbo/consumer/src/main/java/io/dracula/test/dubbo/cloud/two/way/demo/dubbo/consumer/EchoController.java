package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.consumer;

import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.EchoRestService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.EchoService;
import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.SomeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dk
 */
@RestController
public class EchoController {

    private static Logger logger = LoggerFactory.getLogger(EchoController.class);

    @org.apache.dubbo.config.annotation.Reference
    private EchoService echoService;

    @GetMapping("/echoDubbo")
    public SomeMsg echoDubbo(@RequestParam(name="msg", defaultValue="hello") String msg){
        SomeMsg someMsg = new SomeMsg();
        someMsg.setMsg(msg);
        someMsg = echoService.echo(someMsg);
        logger.info("将要返回："+someMsg.getMsg());
        return someMsg;
    }

    @org.apache.dubbo.config.annotation.Reference
    private EchoRestService echoRestService;

    @GetMapping("/echoRest")
    public SomeMsg echoRest(@RequestParam(name="msg", defaultValue="hello") String msg){
        SomeMsg someMsg = new SomeMsg();
        someMsg.setMsg(msg);
        someMsg = echoRestService.echo(someMsg);
        logger.info("将要返回："+someMsg.getMsg());
        return someMsg;
    }

}
