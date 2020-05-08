package io.dracula.test.dubbo.cloud.two.way.demo.cloud.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dk
 */
@RestController
public class EchoController{

    private static Logger logger = LoggerFactory.getLogger(EchoController.class);

    @PostMapping("/echo")
    public MsgInServer echo(@RequestBody MsgInServer msgInServer){
        MsgInServer newMsg = new MsgInServer();
        String newString = "将要返回："+ msgInServer.getMsg();
        logger.info(newString);
        newMsg.setMsg(newString);
        return newMsg;
    }

    /**
     * @author dk
     */
    public static class MsgInServer {

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
