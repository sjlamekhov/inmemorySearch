package platform;

import configuration.ConfigurationService;
import networking.MessageConverter;
import networking.protobuf.GossipServiceClient;
import networking.protobuf.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.SearchService;
import search.cached.CachedSearchService;
import search.withChangesCollecting.ChangesCollectingSearchService;

public class Platform {

    private final ConfigurationService configurationService;
    private final ChangesCollectingSearchService<DocumentUri, Document> searchService;
    private final GossipServiceServer gossipServiceServer;
    private final GossipServiceClient gossipServiceClient;
    private final MessageConverter messageConverter;

    private Platform(
            ConfigurationService configurationService,
            ChangesCollectingSearchService<DocumentUri, Document> searchService,
            GossipServiceServer gossipServiceServer,
            GossipServiceClient gossipServiceClient,
            MessageConverter messageConverter) {
        this.configurationService = configurationService;
        this.searchService = searchService;
        this.gossipServiceServer = gossipServiceServer;
        this.gossipServiceClient = gossipServiceClient;
        this.messageConverter = messageConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public ChangesCollectingSearchService<DocumentUri, Document> getSearchService() {
        return searchService;
    }

    public GossipServiceServer getGossipServiceServer() {
        return gossipServiceServer;
    }

    public GossipServiceClient getGossipServiceClient() {
        return gossipServiceClient;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public static class Builder {
        private ChangesCollectingSearchService<DocumentUri, Document> searchService;
        private GossipServiceServer gossipServiceServer;
        private GossipServiceClient gossipServiceClient;
        private MessageConverter messageConverter;
        private ConfigurationService configurationService;

        protected Builder() {
        }

        public Builder setSearchService(ChangesCollectingSearchService<DocumentUri, Document> searchService) {
            this.searchService = searchService;
            return this;
        }

        public Builder setGossipServiceServer(GossipServiceServer gossipServiceServer) {
            this.gossipServiceServer = gossipServiceServer;
            return this;
        }

        public Builder setGossipServiceClient(GossipServiceClient gossipServiceClient) {
            this.gossipServiceClient = gossipServiceClient;
            return this;
        }

        public Builder setMessageConverter(MessageConverter messageConverter) {
            this.messageConverter = messageConverter;
            return this;
        }

        public Builder setConfigurationService(ConfigurationService configurationService) {
            this.configurationService = configurationService;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Platform build() {
            return new Platform(
                    configurationService,
                    searchService,
                    gossipServiceServer,
                    gossipServiceClient,
                    messageConverter
            );
        }
    }

}
