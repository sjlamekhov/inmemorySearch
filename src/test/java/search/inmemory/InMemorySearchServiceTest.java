package search.inmemory;

import objects.Document;
import objects.DocumentUri;
import search.AbstractSearchServiceTest;
import search.SearchService;

public class InMemorySearchServiceTest extends AbstractSearchServiceTest {

    @Override
    protected SearchService<DocumentUri, Document> getSearchService() {
        return new InMemorySearchService<>();
    }

}