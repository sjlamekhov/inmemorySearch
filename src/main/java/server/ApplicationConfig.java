package server;

import networking.protobuf.GossipServiceClient;
import networking.protobuf.GossipServiceServer;
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
import search.SearchService;

@Configuration
public class ApplicationConfig {

    @Autowired
    private Platform platform;

    @Autowired
    private TaskExecutor taskExecutor;

    @Bean(name = "platform")
    public Platform getPlatform() {
        return PlatformFactory.buildPlatform();
    }

    @Bean(name = "searchService", destroyMethod = "close")
    @DependsOn("platform")
    public SearchService<DocumentUri, Document> getSearchService() {
        return platform.getSearchService();
    }

    @Bean(name = "gossipServiceServer", destroyMethod = "close")
    @DependsOn("platform")
    public GossipServiceServer getGossipServiceServer() {
        return platform.getGossipServiceServer();
    }

    @Bean(name = "gossipServiceClient", destroyMethod = "close")
    @DependsOn("platform")
    public GossipServiceClient getGossipServiceClient() {
        return platform.getGossipServiceClient();
    }

    @Bean(name = "taskExecutor")
    @DependsOn("platform")
    public TaskExecutor taskExecutor() {
        //TODO: init network server and clients in threads
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        return executor;
    }

}
