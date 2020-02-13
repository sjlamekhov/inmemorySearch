package search;

import configuration.ConfigurationService;
import objects.Document;
import objects.DocumentUri;
import search.inmemory.InMemorySearchService;

import java.util.Properties;

public class SearchServiceFactory {

    public static CompositeSearch<DocumentUri, Document> buildSearchService(ConfigurationService configurationService) {
        CompositeSearch<DocumentUri, Document> compositeSearch = new CompositeSearch<>();
        for (String tenant : configurationService.getTenants()) {
            compositeSearch.addService(tenant, new InMemorySearchService<>());
        }
        return compositeSearch;
    }

}
