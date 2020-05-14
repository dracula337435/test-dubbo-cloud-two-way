package io.dracula.test.dubbo.cloud.two.way.plugin;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;

/**
 * @author dk
 */
public class ToCloudTsfConsulRegistry extends ToCloudTsfConsulRegistryBase{

    public ToCloudTsfConsulRegistry(URL url){
        super(url);
    }

    /**
     * 本来是用了provider端url中的interface=*，让url匹配校验通过，但这个特性在2.7之前没有。<br>
     * 换个思路，改consumer端url中的interface=*
     * @param url
     * @param listener
     */
    @Override
    public void doSubscribe(URL url, NotifyListener listener) {
        //注意，看源码URL#addParameter是new了一个新URL，原URL不变
        URL newUrl = url.addParameter("interface", "*");
        super.doSubscribe(newUrl, listener);
    }

}
