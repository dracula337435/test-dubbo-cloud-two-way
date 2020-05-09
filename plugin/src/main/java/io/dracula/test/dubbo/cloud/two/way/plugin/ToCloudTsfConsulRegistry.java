package io.dracula.test.dubbo.cloud.two.way.plugin;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.CatalogServicesRequest;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.tencent.tsf.registry.consul.ConsulRegistry;
import com.tencent.tsf.util.CommonUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.support.FailbackRegistry;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.apache.dubbo.common.Constants.ANY_VALUE;

/**
 * @author dk
 */
public class ToCloudTsfConsulRegistry extends FailbackRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);

    // properties key
    private static final String TOKEN_KEY = "tsf_token";
    private static final String CONSUL_IP_KEY = "tsf_consul_ip";
    private static final String CONSUL_PORT_KEY = "tsf_consul_port";

    // meta key
    private static final String DUBBO_URL_KEY_SPLITS_SIZE = "TSF_DUBBO_URL_SPLITS_SIZE";
    private static final String DUBBO_URL_KEY = "TSF_DUBBO_URL";
    private static final String DUBBO_SDK_KEY = "TSF_DUBBO_SDK";
    private static final String INSTANCE_ID_KEY = "TSF_INSTNACE_ID"; // 注意，consul meta key 里 instance 拼错了，为 instnace，但 agent 又是对的
    private static final String NAMESPACE_ID_KEY = "TSF_NAMESPACE_ID";
    private static final String GROUP_ID_KEY = "TSF_GROUP_ID";
    private static final String APPLICATION_ID_KEY = "TSF_APPLICATION_ID";
    private static final String PROG_VERSION_KEY = "TSF_PROG_VERSION";
    private static final String REGION_KEY = "TSF_REGION";
    private static final String ZONE_KEY = "TSF_ZONE";

    private static final String WATCH_TIMEOUT = "consul-watch-timeout";
    private static final String DUBBO_SERVICEID_PREFIX = "dubbo-";
    // -a 为 alibaba, -e 为 apache
    private static final String DUBBO_SDK_VALUE = "1.1.6.3-e";

    private static final int LOCAL_CONSUL_VALUE_LIMIT = 500;

    private static final int DEFAULT_PORT = 8500;
    // default watch timeout in millisecond
    private static final int DEFAULT_WATCH_TIMEOUT = 30 * 1000;

    private ConsulClient client;

    private ExecutorService notifierExecutor = newCachedThreadPool(
            new NamedThreadFactory("dubbo-consul-notifier", true));
    private ConcurrentMap<URL, ConsulNotifier> notifiers = new ConcurrentHashMap<>();

    private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(
            1, new NamedThreadFactory("DubboRegistryExpireTimer", true));

    private final ScheduledFuture<?> expireFuture;

    private final int checkPeriod;

    // properties value
    private final String TSF_ACL_TOKEN = getPropertyEnvValue(TOKEN_KEY);
    private final String TSF_INSTANCE_ID = getPropertyEnvValue("tsf_instance_id");
    private final String TSF_NAMESPACE_ID = getPropertyEnvValue(NAMESPACE_ID_KEY.toLowerCase());
    private final String TSF_GROUP_ID = getPropertyEnvValue(GROUP_ID_KEY.toLowerCase());
    private final String TSF_APPLICATION_ID = getPropertyEnvValue(APPLICATION_ID_KEY.toLowerCase());
    private final String TSF_PROG_VERSION = getPropertyEnvValue(PROG_VERSION_KEY.toLowerCase());
    private final String TSF_REGION = getPropertyEnvValue(REGION_KEY.toLowerCase());
    private final String TSF_ZONE = getPropertyEnvValue(ZONE_KEY.toLowerCase());

    private final static String CONSUL_NAME_SEPARATOR = "-";

    public ToCloudTsfConsulRegistry(URL url) {
        super(url);

        try {
            String tsfConsulIp = getPropertyEnvValue(CONSUL_IP_KEY);
            String tsfConsulPortStr = getPropertyEnvValue(CONSUL_PORT_KEY);

            if (StringUtils.isNotEmpty(tsfConsulIp) && StringUtils.isNotEmpty(tsfConsulPortStr)) {
                Integer tsfConsulPort = Integer.valueOf(tsfConsulPortStr);
                url = url.setAddress(tsfConsulIp + ":" + tsfConsulPort);
            }
        } catch (Exception ex) {
            logger.warn("get tsf consul ip port error, ex:", ex);
        }

        String host = url.getHost();
        int port = url.getPort() != 0 ? url.getPort() : DEFAULT_PORT;
        logger.debug("consul(" + host + ":" + port + ")");
        client = new ConsulClient(host, port);
        setUrl(url);

        checkPeriod = url.getParameter(Constants.SESSION_TIMEOUT_KEY, Constants.DEFAULT_SESSION_TIMEOUT);
        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    check(); // 延长过期时间
                } catch (Throwable t) {
                    logger.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
                }
            }
        }, checkPeriod / 2, checkPeriod / 2, TimeUnit.MILLISECONDS);
    }

    @Override
    public void register(URL url) {
        if (isConsumerSide(url)) {
            return;
        }
        super.register(toValidLocalURL(url));
    }

    @Override
    public void doRegister(URL url) {
        client.agentServiceRegister(buildService(url), TSF_ACL_TOKEN);
        client.agentCheckPass(toCheckId(url), null, TSF_ACL_TOKEN);
    }

    @Override
    public void unregister(URL url) {
        if (isConsumerSide(url)) {
            return;
        }
        super.unregister(toValidLocalURL(url));
    }

    @Override
    public void doUnregister(URL url) {
        client.agentServiceDeregister(toServiceId(url), TSF_ACL_TOKEN);
    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {
        if (isProviderSide(url)) {
            return;
        }
        super.subscribe(url, listener);
    }

    @Override
    public void doSubscribe(URL url, NotifyListener listener) {
        Long index;
        List<URL> urls;

        if (ANY_VALUE.equals(url.getServiceInterface())) {
            Response<Map<String, List<String>>> response = getAllServices(-1, buildWatchTimeout(url));
            index = response.getConsulIndex();
            List<HealthService> services = getHealthServices(response.getValue());
            urls = convert(services);
        } else {
            String service = toServiceName(url);
            Response<List<HealthService>> response = getHealthServices(service, -1, buildWatchTimeout(url));
            index = response.getConsulIndex();
            urls = convert(response.getValue());
        }
        notify(url, listener, urls);
        ConsulNotifier notifier = notifiers.computeIfAbsent(url, k -> new ConsulNotifier(url, index));
        notifierExecutor.submit(notifier);
    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        if (isProviderSide(url)) {
            return;
        }
        super.unsubscribe(url, listener);
    }

    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
        ConsulNotifier notifier = notifiers.remove(url);
        notifier.stop();
    }

    @Override
    public boolean isAvailable() {
        return client.getAgentSelf() != null && !notifierExecutor.isShutdown();
    }

    @Override
    public void destroy() {
        super.destroy();
        notifierExecutor.shutdown();
    }

    // 由于 ifconfig 有多个 host 时（如虚机上同时也装了 docker），前面的方法可能会拿错
    // 且无法简单地通过 NetUtils.isValidLocalHost 来判断，因此所有都需要通过 getValidLocalHost 获取正确的 host
    private URL toValidLocalURL(URL url) {
        String validHost = CommonUtils.getValidLocalHost(url.getHost());
        logger.info("valid host: " + validHost);
        return url.setHost(validHost);
    }

    private Response<List<HealthService>> getHealthServices(String service, long index, int watchTimeout) {
        HealthServicesRequest request = HealthServicesRequest.newBuilder()
                .setQueryParams(new QueryParams(watchTimeout, index))
                .setPassing(true)
                .setToken(TSF_ACL_TOKEN)
                .build();
        return client.getHealthServices(service, request);
    }

    private Response<Map<String, List<String>>> getAllServices(long index, int watchTimeout) {
        CatalogServicesRequest request = CatalogServicesRequest.newBuilder()
                .setQueryParams(new QueryParams(watchTimeout, index))
                .setToken(TSF_ACL_TOKEN)
                .build();
        return client.getCatalogServices(request);
    }

    private List<HealthService> getHealthServices(Map<String, List<String>> services) {
        return services.keySet().stream()
                .map(s -> getHealthServices(s, -1, -1).getValue())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    private boolean isConsumerSide(URL url) {
        return url.getProtocol().equals(Constants.CONSUMER_PROTOCOL);
    }

    private boolean isProviderSide(URL url) {
        return url.getProtocol().equals(Constants.PROVIDER_PROTOCOL);
    }

    private List<URL> convert(List<HealthService> services) {
        // 本地模式，拼接 URL
        if (StringUtils.isEmpty(TSF_ACL_TOKEN)) {
            return services.stream()
                    .map(s -> {
                        Map<String, String> metas = s.getService().getMeta();
                        int size = Integer.valueOf(metas.get(DUBBO_URL_KEY_SPLITS_SIZE));
                        String fullUrl = "";
                        for (int i = 0; i < size; i++) {
                            fullUrl += metas.get(DUBBO_URL_KEY + i);
                        }
                        return fullUrl;
                    })
                    .map(URL::valueOf)
                    .collect(Collectors.toList());
        } else {
            return services.stream()
                    .map(s -> s.getService().getMeta().get(DUBBO_URL_KEY))
                    .map(URL::valueOf)
                    .collect(Collectors.toList());
        }
    }

    private NewService buildService(URL url) {
        NewService service = new NewService();
        service.setAddress(url.getHost());
        service.setPort(url.getPort());
        service.setId(toServiceId(url));
        service.setName(toServiceName(url));
        service.setCheck(buildCheck(url));
        service.setMeta(buildMeta(url));
        return service;
    }

    private Map<String, String> buildMeta(URL url) {
        Map<String, String> metas = new HashMap<>();
        // 本地模式，如果 URL 过长需要拆分
        if (StringUtils.isEmpty(TSF_ACL_TOKEN)) {
            List<String> splits = CommonUtils.getStrList(url.toFullString(), LOCAL_CONSUL_VALUE_LIMIT);
            metas.put(DUBBO_URL_KEY_SPLITS_SIZE, "" + splits.size());
            for(int i = 0; i < splits.size(); i++) {
                metas.put(DUBBO_URL_KEY + i, splits.get(i));
            }
        } else {
            metas.put(DUBBO_URL_KEY, url.toFullString());
        }
        metas.put(INSTANCE_ID_KEY, TSF_INSTANCE_ID);
        metas.put(GROUP_ID_KEY, TSF_GROUP_ID);
        metas.put(APPLICATION_ID_KEY, TSF_APPLICATION_ID);
        metas.put(PROG_VERSION_KEY, TSF_PROG_VERSION);
        metas.put(REGION_KEY, TSF_REGION);
        metas.put(ZONE_KEY, TSF_ZONE);
        metas.put(DUBBO_SDK_KEY, DUBBO_SDK_VALUE);
        return metas;
    }

    private String toServiceId(URL url) {
        return (DUBBO_SERVICEID_PREFIX + TSF_INSTANCE_ID + Constants.PATH_SEPARATOR + url.toIdentityString()).
                replaceAll(Constants.PATH_SEPARATOR, CONSUL_NAME_SEPARATOR);
    }

    private String toCheckId(URL url) {
        return "service:" + toServiceId(url);
    }

    private String toServiceName(URL url) {
        return toServiceName(toCategoryPath(url));
    }

    private String toServiceName(String path) {
        int i = path.indexOf(Constants.PATH_SEPARATOR);
        return (path.substring(i + 1).replaceAll(Constants.PATH_SEPARATOR, CONSUL_NAME_SEPARATOR).
                replaceAll("\\.", CONSUL_NAME_SEPARATOR)).toLowerCase();
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url);
    }

    private String toServicePath(URL url) {
        String serviceInterface = url.getServiceInterface();
        return serviceInterface.substring(serviceInterface.lastIndexOf(".") + 1);
    }

    private NewService.Check buildCheck(URL url) {
        NewService.Check check = new NewService.Check();
        check.setTtl(String.format("%ss", new Object[]{Long.valueOf(checkPeriod / 1000)}));
        return check;
    }

    private int buildWatchTimeout(URL url) {
        return url.getParameter(WATCH_TIMEOUT, DEFAULT_WATCH_TIMEOUT) / 1000;
    }

    private static String getPropertyEnvValue(String key) {
        return getPropertyEnvValue(key, "");
    }

    private static String getPropertyEnvValue(String key, String defaultValue) {
        String value = ConfigUtils.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }

    /**
     * consul client 心跳
     */
    protected void check() {
        logger.debug("consul agent ttl check start");
        for (URL url : new HashSet<URL>(getRegistered())) {
            if (!url.toFullString().startsWith("consumer:")) {
                String key = toCheckId(url);
                try {
                    logger.debug("consul agent ttl check, check id:" + key);
                    client.agentCheckPass(key, null, TSF_ACL_TOKEN);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
            } else {
                logger.warn("no check pass consumer");
            }
        }
    }

    /**
     * Notifier 线程，长轮询监听 consulIndex 是否变化，如果有变化立刻重新订阅
     */
    private class ConsulNotifier implements Runnable {
        private URL url;
        private long consulIndex;
        private boolean running;

        ConsulNotifier(URL url, long consulIndex) {
            this.url = url;
            this.consulIndex = consulIndex;
            this.running = true;
        }

        @Override
        public void run() {
            while (this.running) {
                if (ANY_VALUE.equals(url.getServiceInterface())) {
                    processServices();
                } else {
                    processService();
                }
            }
        }

        private void processService() {
            String service = toServiceName(url);
            Response<List<HealthService>> response = getHealthServices(service, consulIndex, buildWatchTimeout(url));
            Long currentIndex = response.getConsulIndex();
            if (currentIndex != null) {
                logger.info("[processService] currentIndex: " + currentIndex + ", oldIndex: " + consulIndex
                        + ", response size: " + response.getValue().size());
                consulIndex = currentIndex;
                List<HealthService> services = response.getValue();
                List<URL> urls = convert(services);
                for (NotifyListener listener : getSubscribed().get(url)) {
                    doNotify(url, listener, urls);
                }
            }
        }

        private void processServices() {
            Response<Map<String, List<String>>> response = getAllServices(consulIndex, buildWatchTimeout(url));
            Long currentIndex = response.getConsulIndex();
            if (currentIndex != null) {
                logger.info("[processServices] currentIndex: " + currentIndex + ", oldIndex: " + consulIndex
                        + ", response size: " + response.getValue().size());
                consulIndex = currentIndex;
                List<HealthService> services = getHealthServices(response.getValue());
                List<URL> urls = convert(services);
                for (NotifyListener listener : getSubscribed().get(url)) {
                    doNotify(url, listener, urls);
                }
            }
        }

        void stop() {
            this.running = false;
        }
    }
}
