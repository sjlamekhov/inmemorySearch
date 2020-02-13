package platform;

import configuration.ConfigurationService;
import networking.MessageConverter;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.withChangesCollecting.ChangesCollectingSearchService;

public class Platform {

    private final ConfigurationService configurationService;
    private final ChangesCollectingSearchService<DocumentUri, Document> searchService;
    private final GossipServiceServer gossipServiceServer;
    private final GossipServiceMultiClient gossipServiceMultiClient;
    private final MessageConverter messageConverter;

    private Platform(
            ConfigurationService configurationService,
            ChangesCollectingSearchService<DocumentUri, Document> searchService,
            GossipServiceServer gossipServiceServer,
            GossipServiceMultiClient gossipServiceMultiClient,
            MessageConverter messageConverter) {
        this.configurationService = configurationService;
        this.searchService = searchService;
        this.gossipServiceServer = gossipServiceServer;
        this.gossipServiceMultiClient = gossipServiceMultiClient;
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

    public GossipServiceMultiClient getGossipServiceMultiClient() {
        return gossipServiceMultiClient;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public static class Builder {
        private ChangesCollectingSearchService<DocumentUri, Document> searchService;
        private GossipServiceServer gossipServiceServer;
        private GossipServiceMultiClient gossipServiceMultiClient;
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

        public Builder setGossipServiceMultiClient(GossipServiceMultiClient gossipServiceMultiClient) {
            this.gossipServiceMultiClient = gossipServiceMultiClient;
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
                    gossipServiceMultiClient,
                    messageConverter
            );
        }
    }

}
