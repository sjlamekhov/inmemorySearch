package search;

import objects.Document;
import objects.DocumentUri;
import search.inmemory.InMemorySearchService;

import java.util.Properties;

public class SearchServiceFactory {

    public static CompositeSearch<DocumentUri, Document> buildSearchService(Properties properties) {
        CompositeSearch<DocumentUri, Document> compositeSearch = new CompositeSearch<>();
        compositeSearch.addService("testTenantId", new InMemorySearchService<>());
        return compositeSearch;
    }

}
