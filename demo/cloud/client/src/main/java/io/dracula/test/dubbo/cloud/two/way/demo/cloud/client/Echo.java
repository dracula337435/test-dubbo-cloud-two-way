package io.dracula.test.dubbo.cloud.two.way.demo.cloud.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author dk
 */
@FeignClient("test-cloud-server")
public interface Echo {

    @PostMapping("/echo")
    MsgInClient echo(MsgInClient msgInClient);

    /**
     * @author dk
     */
    public class MsgInClient{

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
