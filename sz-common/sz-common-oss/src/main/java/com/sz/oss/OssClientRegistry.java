package com.sz.oss;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 按资源场景的有效连接规格提供 OSS 客户端，并管理额外客户端的生命周期。
 */
public class OssClientRegistry implements AutoCloseable {

    private final OssProperties properties;

    private final OssClient defaultClient;

    private final ClientFactory clientFactory;

    private final Map<ClientSpec, ManagedClient> managedClients = new ConcurrentHashMap<>();

    private final Map<String, ClientSpec> sceneSpecs = new ConcurrentHashMap<>();

    private final AtomicBoolean closed = new AtomicBoolean();

    public OssClientRegistry(OssProperties properties, OssClient defaultClient, ClientFactory clientFactory) {
        this.properties = properties;
        this.defaultClient = defaultClient;
        this.clientFactory = clientFactory;
    }

    public OssClient resolve(String sceneCode, OssSceneConnection connection, OssAddressingStyle sceneStyle) {
        if (closed.get()) {
            throw new IllegalStateException("OssClientRegistry 已关闭");
        }
        if (sceneCode == null || sceneCode.isBlank()) {
            throw new IllegalArgumentException("OSS sceneCode 不能为空");
        }
        OssSceneConnection effectiveConnection = connection == null ? new OssSceneConnection() : connection;
        effectiveConnection.validateRequired(sceneCode);
        OssAddressingStyle effectiveStyle = effectiveDefaultStyle(sceneStyle, effectiveConnection.getProvider());
        ClientSpec spec = ClientSpec.from(effectiveConnection, effectiveStyle, properties);
        ClientSpec previous = sceneSpecs.putIfAbsent(sceneCode, spec);
        if (previous != null && !previous.equals(spec)) {
            throw new IllegalStateException("OSS 场景 [" + sceneCode + "] 的连接配置已变化，请重启应用以创建新客户端");
        }
        if (!effectiveConnection.hasIndependentConnection() && spec.equals(ClientSpec.defaultSpec(properties))) {
            return defaultClient;
        }
        return managedClients.computeIfAbsent(spec,
                ignored -> Objects.requireNonNull(clientFactory.create(effectiveConnection, effectiveStyle), "OSS clientFactory 返回了 null ManagedClient"))
                .client();
    }

    private OssAddressingStyle effectiveDefaultStyle(OssAddressingStyle sceneStyle, OssProviderEnum sceneProvider) {
        return properties.resolvePathStyle(sceneStyle, sceneProvider) ? OssAddressingStyle.PATH : OssAddressingStyle.VIRTUAL_HOSTED;
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        RuntimeException failure = null;
        try {
            for (ManagedClient managedClient : managedClients.values()) {
                try {
                    managedClient.close();
                } catch (RuntimeException e) {
                    if (failure == null) {
                        failure = e;
                    } else {
                        failure.addSuppressed(e);
                    }
                }
            }
        } finally {
            managedClients.clear();
            sceneSpecs.clear();
        }
        if (failure != null) {
            throw failure;
        }
    }

    @FunctionalInterface
    public interface ClientFactory {

        ManagedClient create(OssSceneConnection connection, OssAddressingStyle addressingStyle);
    }

    public static final class ManagedClient implements AutoCloseable {

        private final OssClient client;

        private final Runnable closer;

        private final AtomicBoolean closed = new AtomicBoolean();

        public ManagedClient(OssClient client, Runnable closer) {
            this.client = client;
            this.closer = closer;
        }

        public OssClient client() {
            return client;
        }

        @Override
        public void close() {
            if (closed.compareAndSet(false, true)) {
                closer.run();
            }
        }
    }

    private record ClientSpec(String endpoint, String accessKey, String secretKey, String region, OssAddressingStyle addressingStyle, boolean independent) {

        private static ClientSpec from(OssSceneConnection connection, OssAddressingStyle style, OssProperties global) {
            return new ClientSpec(connection.resolveEndpointUri(global).toString(), connection.resolveAccessKey(global), connection.resolveSecretKey(global),
                    normalizeRegion(connection.resolveRegion(global)), style, connection.hasIndependentConnection());
        }

        private static ClientSpec defaultSpec(OssProperties global) {
            OssAddressingStyle style = global.resolvePathStyle() ? OssAddressingStyle.PATH : OssAddressingStyle.VIRTUAL_HOSTED;
            return new ClientSpec(global.resolveEndpointUri().toString(), global.getAccessKey(), global.getSecretKey(), normalizeRegion(global.getRegion()),
                    style, false);
        }

        private static String normalizeRegion(String region) {
            return region == null || region.isBlank() ? "us-east-1" : region.trim();
        }
    }
}
