package org.acme;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class S3Storage
        implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    protected static int S3_PORT = 8333;
    protected static final String IMAGE_NAME = "mystorage:latest";
    protected static final String DATA_DIR = Optional.ofNullable(System.getenv("DATA_DIR")).orElse("/data");
    protected static final Map<String, String> envMap =
            Map.of(
                    "DATA_DIR", DATA_DIR,
                    "IP_BIND", "0.0.0.0",
                    "WEED_V", "4");
    protected final Logger logger = Logger.getLogger(getClass());
    protected Optional<String> containerNetworkId;
    protected GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container =
                new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
                        .withExposedPorts(S3_PORT)
                        .withEnv(envMap)
                        .withTmpFs(Map.of(DATA_DIR, "rw,noexec,nosuid,size=65536k"))
                        .withCommand("server", "-s3", "-s3.config", "/etc/storage.conf.json")
                        .waitingFor(Wait.forLogMessage(".*Start Seaweed S3 API Server.*", 1));
        containerNetworkId.ifPresent(container::withNetworkMode);

        container.start();

        String networkHostPort =
                adjustS3Url(container, container.getHost(), container.getMappedPort(S3_PORT));

        Map<String, String> properties = new HashMap<String, String>();

        properties.put("quarkus.s3.endpoint-override", networkHostPort);
        properties.put("quarkus.s3.path-style-access", "true");
        properties.put("quarkus.s3.aws.credentials.type", "static");
        properties.put("quarkus.s3.aws.credentials.static-provider.access-key-id", "accesskey");
        properties.put("quarkus.s3.aws.credentials.static-provider.secret-access-key", "secretkey");

        // FIXME since Quarkus 3.20 / S3 SDK 2.30.36 leaving this enabled results in junk
        // 'chunk-signature' data being inserted to PutObjectRequests
        properties.put("quarkus.s3.checksum-validation", Optional.ofNullable(System.getProperty("quarkus.s3.checksum-validation", System.getenv("QUARKUS_S3_CHECKSUM_VALIDATION"))).orElse("true"));
        properties.put("quarkus.s3.chunked-encoding", Optional.ofNullable(System.getProperty("quarkus.s3.chunked-encoding", System.getenv("QUARKUS_S3_CHUNKED_ENCODING"))).orElse("true"));

        return properties;
    }

    @Override
    public void stop() {
        logger.info("stopping");
        if (container != null) {
            container.stop();
            container.close();
        }
        logger.info("stopped");
    }

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        containerNetworkId = context.containerNetworkId();
    }

    protected String adjustS3Url(GenericContainer<?> container, String host, int port) {
        return "http://" + container.getHost() + ":" + container.getMappedPort(S3_PORT);
    }
}
