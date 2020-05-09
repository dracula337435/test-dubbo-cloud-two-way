package io.dracula.test.dubbo.cloud.two.way.plugin;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;

/**
 * 照抄了com.tencent.tsf:tsf-dubbo:1.1.6-apache-RELEASE中的{@link com.tencent.tsf.registry.consul.ConsulRegistryFactory}
 *
 * @author dk
 */
public class ToCloudTsfConsulRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        return new ToCloudTsfConsulRegistry(url);
    }

}