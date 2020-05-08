# 试验，dubbo和cloud双向互调

不经过网关，dubbo换rest，和cloud共用一个注册中心，直接互相调用

```dubbo-rest```的依赖调整了一下
```consumer```依赖```api```依赖```jetty-all```，会报异常
```
2020-05-08 15:42:24.788 ERROR 11524 --- [nio-8080-exec-2] o.a.c.c.C.[Tomcat].[localhost]           : Exception Processing /favicon.ico

java.lang.SecurityException: AuthConfigFactory error: java.lang.ClassNotFoundException: org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl
	at javax.security.auth.message.config.AuthConfigFactory.getFactory(AuthConfigFactory.java:75) ~[javax.security.auth.message-1.0.0.v201108011116.jar:1.0]
	at org.apache.catalina.authenticator.AuthenticatorBase.findJaspicProvider(AuthenticatorBase.java:1239) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.authenticator.AuthenticatorBase.getJaspicProvider(AuthenticatorBase.java:1232) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:481) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:834) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1415) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) ~[na:na]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) ~[na:na]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at java.base/java.lang.Thread.run(Thread.java:834) ~[na:na]
Caused by: java.lang.ClassNotFoundException: org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl
	at org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader.loadClass(TomcatEmbeddedWebappClassLoader.java:70) ~[spring-boot-2.1.3.RELEASE.jar:2.1.3.RELEASE]
	at org.apache.catalina.loader.WebappClassLoaderBase.loadClass(WebappClassLoaderBase.java:1186) ~[tomcat-embed-core-9.0.16.jar:9.0.16]
	at java.base/java.lang.Class.forName0(Native Method) ~[na:na]
	at java.base/java.lang.Class.forName(Class.java:398) ~[na:na]
	at javax.security.auth.message.config.AuthConfigFactory$3.run(AuthConfigFactory.java:66) ~[javax.security.auth.message-1.0.0.v201108011116.jar:1.0]
	at java.base/java.security.AccessController.doPrivileged(Native Method) ~[na:na]
	at javax.security.auth.message.config.AuthConfigFactory.getFactory(AuthConfigFactory.java:62) ~[javax.security.auth.message-1.0.0.v201108011116.jar:1.0]
	... 16 common frames omitted
```
但是，如果多指定一下```consumer```也依赖```jetty-all```，就不会有这个异常了  
依赖链条为，```consumer```依赖```jetty-all```，```consumer```依赖```api```依赖```jetty-all```