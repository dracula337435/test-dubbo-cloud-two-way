package io.dracula.test.dubbo.cloud.two.way.demo.cloud.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author dk
 */
@FeignClient("echoRestService")
public interface EchoDubbo {

    @PostMapping("/echo")
    EchoCloud.MsgInClient echo(EchoCloud.MsgInClient msgInClient);

}
