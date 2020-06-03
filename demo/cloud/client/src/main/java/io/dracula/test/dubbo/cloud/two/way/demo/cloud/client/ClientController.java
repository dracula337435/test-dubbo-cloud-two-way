package io.dracula.test.dubbo.cloud.two.way.demo.cloud.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author dk
 */
@RestController
public class ClientController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private EchoCloud echoCloud;

    @GetMapping("echoFeignCloud")
    public EchoCloud.MsgInClient echoFeignCloud(@RequestParam(name="msg", defaultValue="hello") String msg){
        EchoCloud.MsgInClient msgInClient = new EchoCloud.MsgInClient();
        msgInClient.setMsg(msg);
        EchoCloud.MsgInClient echoMsg = echoCloud.echo(msgInClient);
        logger.info("echoFeignCloud将要返回："+echoMsg.getMsg());
        return echoMsg;
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("echoTemplateCloud")
    public EchoCloud.MsgInClient echoTemplateCloud(@RequestParam(name="msg", defaultValue="hello") String msg){
        EchoCloud.MsgInClient msgInClient = new EchoCloud.MsgInClient();
        msgInClient.setMsg(msg);
        EchoCloud.MsgInClient echoMsg = restTemplate.postForObject("http://test-cloud-server/echo", msgInClient, EchoCloud.MsgInClient.class);
        logger.info("echoTemplateCloud将要返回："+echoMsg.getMsg());
        return echoMsg;
    }

    @Autowired
    private EchoDubbo echoDubbo;

    @GetMapping("echoFeignDubbo")
    public EchoCloud.MsgInClient echoFeignDubbo(@RequestParam(name="msg", defaultValue="hello") String msg){
        EchoCloud.MsgInClient msgInClient = new EchoCloud.MsgInClient();
        msgInClient.setMsg(msg);
        EchoCloud.MsgInClient echoMsg = echoDubbo.echo(msgInClient);
        logger.info("echoFeignDubbo将要返回："+echoMsg.getMsg());
        return echoMsg;
    }

    @GetMapping("echoTemplateDubbo")
    public EchoCloud.MsgInClient echoTemplateDubbo(@RequestParam(name="msg", defaultValue="hello") String msg){
        EchoCloud.MsgInClient msgInClient = new EchoCloud.MsgInClient();
        msgInClient.setMsg(msg);
        EchoCloud.MsgInClient echoMsg = restTemplate.postForObject("http://echorestservice/echo", msgInClient, EchoCloud.MsgInClient.class);
        logger.info("echoTemplateDubbo将要返回："+echoMsg.getMsg());
        return echoMsg;
    }

}
