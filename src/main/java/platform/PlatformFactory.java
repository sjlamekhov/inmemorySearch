package platform;

import configuration.ConfigurationService;
import networking.GossipServiceClient;
import networking.MessageConverterJson;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.SearchServiceFactory;
import search.withChangesCollecting.ChangesCollectingSearchService;

import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatform(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(properties);

        GossipServiceServer gossipServiceServer = new GossipServiceServer(configurationService.getServerPort());

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

        return Platform.Builder.newInstance()
                .setConfigurationService(configurationService)
                .setGossipServiceServer(gossipServiceServer)
                .setGossipServiceMultiClient(gossipServiceMultiClient)
                .setSearchService(searchService)
                .setMessageConverter(new MessageConverterJson())
                .build();
    }

}
