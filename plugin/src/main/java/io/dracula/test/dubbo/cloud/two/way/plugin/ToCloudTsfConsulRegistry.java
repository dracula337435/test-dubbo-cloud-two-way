package io.dracula.test.dubbo.cloud.two.way.plugin;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;

import java.util.List;

/**
 * @author dk
 */
public class ToCloudTsfConsulRegistry implements Registry {

    public ToCloudTsfConsulRegistry(URL url){

    }

    public URL getUrl() {
        return null;
    }

    public boolean isAvailable() {
        return false;
    }

    public void destroy() {

    }

    public void register(URL url) {

    }

    public void unregister(URL url) {

    }

    public void subscribe(URL url, NotifyListener listener) {

    }

    public void unsubscribe(URL url, NotifyListener listener) {

    }

    public List<URL> lookup(URL url) {
        return null;
    }
}
