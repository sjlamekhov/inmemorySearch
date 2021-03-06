package search;

import configuration.ConfigurationService;
import dao.UriGenerator;
import objects.Document;
import objects.DocumentUri;
import search.cached.CachedSearchService;
import search.inmemory.InMemorySearchService;
import search.sharded.ShardedSearchService;
import sharding.ShardingService;
import sharding.client.RestSearchClient;

import java.util.Properties;

public class SearchServiceFactory {

    public static CompositeSearch<DocumentUri, Document> buildSearchService(ConfigurationService configurationService) {
        boolean useCache = configurationService.isUseCache();
        boolean useSharding = configurationService.getOperationalMode() == ConfigurationService.OperationMode.sharding;
        CompositeSearch<DocumentUri, Document> compositeSearch = new CompositeSearch<>();
        for (String tenant : configurationService.getTenants()) {
            UriGenerator uriGenerator = new UriGenerator(configurationService.getMaxUriLength());
            SearchService<DocumentUri, Document> innerSeachService = useCache ?
                    new CachedSearchService<>(new InMemorySearchService<>(uriGenerator))
                    : new InMemorySearchService<>(uriGenerator);
            if (useSharding) {
                compositeSearch.addService(tenant, new ShardedSearchService<>(
                        innerSeachService,
                        new ShardingService<>(new RestSearchClient(configurationService.getClusterNodes())))
                );
            } else {
                compositeSearch.addService(tenant, innerSeachService);
            }
        }
        return compositeSearch;
    }

}
