package io.dracula.test.dubbo.cloud.two.way.demo.cloud.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dk
 */
@RestController
public class ClientController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private Echo echo;

    @GetMapping("echoFeignCloud")
    public Echo.MsgInClient echoFeignCloud(@RequestParam(name="msg", defaultValue="hello") String msg){
        Echo.MsgInClient msgInClient = new Echo.MsgInClient();
        msgInClient.setMsg(msg);
        Echo.MsgInClient echoMsg = echo.echo(msgInClient);
        logger.info("echoFeignCloud将要返回："+echoMsg.getMsg());
        return echoMsg;
    }

}
