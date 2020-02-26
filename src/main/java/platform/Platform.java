package platform;

import configuration.ConfigurationService;
import networking.MessageConverter;
import networking.GossipServiceMultiClient;
import networking.GossipServiceServer;
import objects.Document;
import objects.DocumentUri;
import search.withChangesCollecting.ChangesCollectingSearchService;
import search.request.SearchRequestLimitations;

public class Platform {

    private final ConfigurationService configurationService;
    private final ChangesCollectingSearchService<DocumentUri, Document> searchService;
    private final SearchRequestLimitations searchRequestLimitations;
    private final GossipServiceServer gossipServiceServer;
    private final GossipServiceMultiClient gossipServiceMultiClient;
    private final MessageConverter messageConverter;
    private final StatusService statusService;

    private Platform(
            ConfigurationService configurationService,
            ChangesCollectingSearchService<DocumentUri, Document> searchService,
            SearchRequestLimitations searchRequestLimitations,
            GossipServiceServer gossipServiceServer,
            GossipServiceMultiClient gossipServiceMultiClient,
            MessageConverter messageConverter, StatusService statusService) {
        this.configurationService = configurationService;
        this.searchService = searchService;
        this.searchRequestLimitations = searchRequestLimitations;
        this.gossipServiceServer = gossipServiceServer;
        this.gossipServiceMultiClient = gossipServiceMultiClient;
        this.messageConverter = messageConverter;
        this.statusService = statusService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public ChangesCollectingSearchService<DocumentUri, Document> getSearchService() {
        return searchService;
    }

    public SearchRequestLimitations getSearchRequestLimitations() {
        return searchRequestLimitations;
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

    public StatusService getStatusService() {
        return statusService;
    }

    public static class Builder {
        private ChangesCollectingSearchService<DocumentUri, Document> searchService;
        private SearchRequestLimitations searchRequestLimitations;
        private GossipServiceServer gossipServiceServer;
        private GossipServiceMultiClient gossipServiceMultiClient;
        private MessageConverter messageConverter;
        private ConfigurationService configurationService;
        private StatusService statusService;

        protected Builder() {
        }

        public Builder setSearchRequestLimitations(SearchRequestLimitations searchRequestLimitations) {
            this.searchRequestLimitations = searchRequestLimitations;
            return this;
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

        public Builder setStatusServer(StatusService statusService) {
            this.statusService = statusService;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Platform build() {
            return new Platform(
                    configurationService,
                    searchService,
                    searchRequestLimitations,
                    gossipServiceServer,
                    gossipServiceMultiClient,
                    messageConverter,
                    statusService
            );
        }
    }

}
