package io.dracula.test.dubbo.cloud.two.way.plugin;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;

/**
 * @author dk
 */
public class ToCloudTsfConsulRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        return new ToCloudTsfConsulRegistry(url);
    }

}