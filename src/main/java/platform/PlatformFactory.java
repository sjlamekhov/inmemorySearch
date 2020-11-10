package platform;

import configuration.ConfigurationService;
import networking.GossipServiceClient;
import networking.DocumentMessageConverterJson;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import dump.DumpService;
import search.request.SearchRequestLimitations;
import search.SearchServiceFactory;
import search.withChangesCollecting.ChangesCollectingSearchService;

import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatform(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(properties);

        GossipServiceServer gossipServiceServer = new GossipServiceServer(configurationService.getGossipServerPort());

        GossipServiceMultiClient gossipServiceMultiClient = new GossipServiceMultiClient();
        for (String clusterNodeAddress : configurationService.getClusterNodes()) {
            String[] splitted = clusterNodeAddress.split(":");
            if (splitted.length != 2) {
                continue;
            }
            String host = splitted[0];
            int port = Integer.valueOf(splitted[1]);
            gossipServiceMultiClient.registerClient(clusterNodeAddress, new GossipServiceClient(host, port));
        }

        ChangesCollectingSearchService<DocumentUri, Document> searchService = new ChangesCollectingSearchService<>(
                SearchServiceFactory.buildSearchService(configurationService)
        );

        SearchRequestLimitations searchRequestLimitations = new SearchRequestLimitations(
                configurationService.getMaxSearchRequestDepth(),
                configurationService.getMaxSearchRequestSize()
        );

        ThreadPoolTaskExecutor executor =  new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        executor.initialize();

        return Platform.Builder.newInstance()
                .setConfigurationService(configurationService)
                .setGossipServiceServer(gossipServiceServer)
                .setGossipServiceMultiClient(gossipServiceMultiClient)
                .setSearchService(searchService)
                .setSearchRequestLimitations(searchRequestLimitations)
                .setMessageConverter(new DocumentMessageConverterJson())
                .setStatusService(new StatusService(gossipServiceServer, gossipServiceMultiClient))
                .setDumpService(new DumpService<>(executor, searchService))
                .setExecutor(executor)
                .build();
    }

}
