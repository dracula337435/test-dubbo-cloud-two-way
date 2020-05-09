package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.consumer;

import io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api.SomeMsg;
import io.dracula.test.dubbo.cloud.two.way.plugin.ToCloudTsfConsulServiceName;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author dk
 */
@ToCloudTsfConsulServiceName("test-cloud-server")
@Path("/echo")
public interface TestToCloudEchoService {

    @POST
    @Consumes(ContentType.APPLICATION_JSON_UTF_8)
    @Produces(ContentType.APPLICATION_JSON_UTF_8)
    SomeMsg echo(SomeMsg someMsg);

}
