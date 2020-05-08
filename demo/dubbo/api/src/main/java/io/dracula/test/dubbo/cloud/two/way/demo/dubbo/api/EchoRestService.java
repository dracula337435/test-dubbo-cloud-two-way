package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api;

import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author dk
 */
@Path("/echo")
public interface EchoRestService {

    @POST
    @Consumes(ContentType.APPLICATION_JSON_UTF_8)
    @Produces(ContentType.APPLICATION_JSON_UTF_8)
    SomeMsg echo(SomeMsg someMsg);

}
