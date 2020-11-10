package server;

import configuration.ConfigProvider;
import configuration.ConfigurationService;
import dump.DumpService;
import networking.Message;
import networking.NetworkDispatcher;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import platform.Platform;
import platform.PlatformFactory;
import platform.StatusService;
import search.SearchService;
import search.request.SearchRequestLimitations;

import java.util.Properties;

@Configuration
public class ApplicationConfig {

    private final Properties properties;

    @Autowired
    private Platform platform;

    @Autowired
    private NetworkDispatcher networkDispatcher;

    @Autowired
    private TaskExecutor taskExecutor;

    public ApplicationConfig() {
        properties = ConfigProvider.getProperties();
    }

    @Bean(name = "platform")
    public Platform getPlatform() {
        return PlatformFactory.buildPlatform(properties);
    }

    @Bean(name = "configurationService")
    public ConfigurationService getConfigurationService() {
        return platform.getConfigurationService();
    }

    @Bean(name = "searchService", destroyMethod = "close")
    @DependsOn("platform")
    public SearchService<DocumentUri, Document> getSearchService() {
        return platform.getSearchService();
    }

    @Bean(name = "searchRequestLimitations")
    @DependsOn("platform")
    public SearchRequestLimitations getSearchRequestLimitations() {
        return platform.getSearchRequestLimitations();
    }

    @Bean(name = "gossipServiceServer", destroyMethod = "close")
    @DependsOn("platform")
    public GossipServiceServer getGossipServiceServer() {
        return platform.getGossipServiceServer();
    }

    @Bean(name = "gossipServiceMultiClient", destroyMethod = "close")
    @DependsOn("platform")
    public GossipServiceMultiClient getGossipServiceMultiClient() {
        return platform.getGossipServiceMultiClient();
    }

    @Bean(name = "statusService")
    @DependsOn("platform")
    public StatusService getStatusService() {
        return platform.getStatusService();
    }

    @Bean(name = "dumpService")
    @DependsOn("platform")
    public DumpService getDumpService() {
        return platform.getDumpService();
    }

    @Bean(name = "networkDispatcher")
    @DependsOn("platform")
    public NetworkDispatcher networkDispatcher() {
        return new NetworkDispatcher(
                () -> platform.getMessageConverter().convertToMessage(
                        platform.getGossipServiceServer().getIncomingQueue().poll()
                ),
                () -> platform.getSearchService().pollLocalChanges(),
                message -> platform.getGossipServiceMultiClient().sendChange(
                        platform.getMessageConverter().convertToChangeRequest(message)
                ),
                message -> {
                    if (null == message) {
                        return;
                    }
                    if (Message.MessageType.CREATE == message.getMessageType()) {
                        platform.getSearchService().addObjectToIndex(message.getObject().getUri().getTenantId(), (Document) message.getObject());
                    } else {
                        platform.getSearchService().removeObjectFromIndex((Document) message.getObject());
                    }
                }
        );
    }

    @Bean(name = "taskExecutor")
    @DependsOn("networkDispatcher")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = platform.getExecutor();
        if (platform.getConfigurationService().getOperationalMode() == ConfigurationService.OperationMode.reliability) {
            executor.execute(() -> {
                try {
                    platform.getGossipServiceMultiClient().init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.execute(() -> {
                try {
                    platform.getGossipServiceServer().init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.execute(() -> {
                try {
                    networkDispatcher.receiveAndSendMessages(false);
                    Thread.sleep(100000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return executor;
    }

}
