package benchmarks.searchService;

import objects.Document;
import objects.DocumentUri;
import org.openjdk.jmh.annotations.*;
import search.SearchService;
import search.cached.CachedSearchService;
import search.inmemory.InMemorySearchService;

@State(Scope.Benchmark)
public class CachedWithOptimizerSearchExecutionPlan extends BaseSearchExecutionPlan {

    @Param({ "100"})
    public int iterations;

    public SearchService<DocumentUri, Document> searchService;

    protected SearchService<DocumentUri, Document> getSearchService() {
        return new CachedSearchService<>(new InMemorySearchService<>());
    }

    @Setup(Level.Invocation)
    public void setUp() {
        searchService = getSearchService();
        initSearchService(searchService);
    }

}
