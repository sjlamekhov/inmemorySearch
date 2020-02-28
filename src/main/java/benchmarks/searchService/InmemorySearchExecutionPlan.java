package benchmarks.searchService;

import objects.Document;
import objects.DocumentUri;
import org.openjdk.jmh.annotations.*;
import search.SearchService;
import search.inmemory.InMemorySearchService;

import java.util.Iterator;

@State(Scope.Benchmark)
public class InmemorySearchExecutionPlan extends BaseSearchExecutionPlan {

    @Param({ "100"})
    public int iterations;

    public SearchService<DocumentUri, Document> searchService;

    protected SearchService<DocumentUri, Document> getSearchService() {
        return new InMemorySearchService<>();
    }

    @Setup(Level.Invocation)
    public void setUp() {
        searchService = getSearchService();
        initSearchService(searchService);
    }

}
