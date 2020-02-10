package platform;

import configuration.ConfigurationService;
import networking.MessageConverterJson;
import networking.protobuf.GossipServiceClient;
import networking.protobuf.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.SearchService;
import search.SearchServiceFactory;
import search.cached.CachedSearchService;
import search.withChangesCollecting.ChangesCollectingSearchService;

import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatform(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(properties);

        GossipServiceServer gossipServiceServer = new GossipServiceServer();

        GossipServiceClient gossipServiceClient = new GossipServiceClient();

        ChangesCollectingSearchService<DocumentUri, Document> searchService = new ChangesCollectingSearchService<>(
                SearchServiceFactory.buildSearchService(new Properties())
        );

        return Platform.Builder.newInstance()
                .setConfigurationService(configurationService)
                .setGossipServiceServer(gossipServiceServer)
                .setGossipServiceClient(gossipServiceClient)
                .setSearchService(searchService)
                .setMessageConverter(new MessageConverterJson())
                .build();
    }

}
