package platform;

import configuration.ConfigurationService;
import networking.MessageConverterJson;
import networking.protobuf.GossipServiceMultiClient;
import networking.protobuf.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.SearchServiceFactory;
import search.withChangesCollecting.ChangesCollectingSearchService;

import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatform(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(properties);

        GossipServiceServer gossipServiceServer = new GossipServiceServer();

        GossipServiceMultiClient gossipServiceMultiClient = new GossipServiceMultiClient();

        ChangesCollectingSearchService<DocumentUri, Document> searchService = new ChangesCollectingSearchService<>(
                SearchServiceFactory.buildSearchService(new Properties())
        );

        return Platform.Builder.newInstance()
                .setConfigurationService(configurationService)
                .setGossipServiceServer(gossipServiceServer)
                .setGossipServiceMultiClient(gossipServiceMultiClient)
                .setSearchService(searchService)
                .setMessageConverter(new MessageConverterJson())
                .build();
    }

}
