package server;

import networking.Message;
import networking.NetworkDispatcher;
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

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @Bean(name = "networkDispatcher")
    @DependsOn("taskExecutor")
    public NetworkDispatcher networkDispatcher() {
        return new NetworkDispatcher(
                () -> platform.getMessageConverter().convertToMessage(
                        platform.getGossipServiceServer().getIncomingQueue().poll()
                ),
                () -> platform.getSearchService().pollLocalChanges(),
                message -> platform.getGossipServiceClient().sendChange(
                        platform.getMessageConverter().convertToChangeRequest(message)
                ),
                new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        if (null == message) {
                            return;
                        }
                        if (Message.MessageType.CREATE == message.getMessageType()) {
                            platform.getSearchService().addObjectToIndex((Document) message.getAbstractObject());
                        } else {
                            platform.getSearchService().removeObjectFromIndex((Document) message.getAbstractObject());
                        }
                    }
                }
        );
    }

    @Bean(name = "taskExecutor")
    @DependsOn("platform")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        executor.initialize();
        executor.execute(() -> {
            try {
                platform.getGossipServiceClient().init();
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
        return executor;
    }

}
