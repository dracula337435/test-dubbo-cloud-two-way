package io.dracula.test.dubbo.cloud.two.way.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于dubbo调cloud时，去consul上找服务名。dubbo侧接口短名和cloud的服务名一致即可找到，但是cloud中常用“-”号作服务名，“-”号不能出现在接口名中。<br>
 * 类似情况，用这个注解指定要找的服务名，有更高优先级；如果没有这个注解，则按接口短名全小写
 *
 * @author dk
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToCloudTsfConsulServiceName {

    String value();

}
